package co.wadcorp.waiting.batch.job.settings.operation;

import static co.wadcorp.libs.stream.StreamUtils.groupingByList;
import static co.wadcorp.waiting.data.domain.settings.QOperationTimeSettingsEntity.operationTimeSettingsEntity;
import static co.wadcorp.waiting.data.domain.settings.remote.QRemoteOperationTimeSettingsEntity.remoteOperationTimeSettingsEntity;

import co.wadcorp.waiting.batch.job.settings.operation.parameter.ShopOperationInfoInitBatchJobParameter;
import co.wadcorp.waiting.batch.job.settings.operation.store.ShopOperationInfoInitBatchStore;
import co.wadcorp.waiting.batch.reader.QuerydslPagingItemReader;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInitializeFactory;
import co.wadcorp.waiting.data.query.shop.ShopQueryRepository;
import co.wadcorp.waiting.data.query.waiting.ShopOperationInfoQueryRepository;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.shared.util.ListUtils;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * [운영 정보 초기 설정 데이터 생성 배치]
 * <p/>
 * 기본적으로 하루 1번 오전 시간에 수행된다. 상황에 따라 수시로 수행하기도 한다.
 * <p/>
 * 1. RemoteOperationTimeSettingsEntity 조회하여 원격 운영 시간에 대한 정보 로딩
 * <p>
 * 2. 기존에 운영 정보가 존재하는 shopId는 제외하고 생성 필요한 대상 shopId 추출
 * <p>
 * 3. OperationTimeSettingsEntity 조회
 * <p/>
 * 4. 조회한 시간 설정 정보로 ShopOperationInfo 초기화 생성해서 저장
 * <p/>
 * 5. Slack 결과 전송 (TODO)
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ShopOperationInfoInitBatchConfiguration {

  public static final String JOB_NAME = "ShopOperationInfoInitBatch";
  private static final String BEAN_PREFIX = JOB_NAME + "_";

  private static final int BATCH_SIZE = 100;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final ShopOperationInfoService shopOperationInfoService;
  private final ShopOperationInfoInitBatchJobParameter jobParameter;
  private final ShopOperationInfoInitBatchStore store;
  private final ShopQueryRepository shopQueryRepository;
  private final ShopOperationInfoQueryRepository shopOperationInfoQueryRepository;

  @Value("${chunkSize:1000}")
  public int chunkSize;

  @Bean(JOB_NAME)
  public Job job() {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(loadRemoteOperationInfoStep())
        .next(loadTargetShopIdsStep())
        .next(createOperationInfoStep())
        .next(slackSendStep())
        .build();
  }

  @Bean(BEAN_PREFIX + "loadRemoteOperationInfoStep")
  @JobScope
  public Step loadRemoteOperationInfoStep() {
    return new StepBuilder("loadRemoteOperationInfoStep", jobRepository)
        .<RemoteOperationTimeSettingsEntity, RemoteOperationTimeSettingsEntity>
            chunk(chunkSize, platformTransactionManager)
        .reader(loadRemoteOperationInfoReader())
        .writer(loadRemoteOperationInfoWriter())
        .build();
  }

  @Bean(BEAN_PREFIX + "loadRemoteOperationInfoReader")
  @StepScope
  public QuerydslPagingItemReader<RemoteOperationTimeSettingsEntity> loadRemoteOperationInfoReader() {
    return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize,
        queryFactory -> queryFactory
            .selectFrom(remoteOperationTimeSettingsEntity)
            .where(remoteOperationTimeSettingsEntity.isPublished.isTrue())
    );
  }

  @Bean(BEAN_PREFIX + "loadRemoteOperationInfoWriter")
  @StepScope
  public ItemWriter<RemoteOperationTimeSettingsEntity> loadRemoteOperationInfoWriter() {
    return chunk -> {
      List<RemoteOperationTimeSettingsEntity> items = new ArrayList<>(chunk.getItems());
      store.putRemoteOperationTimeSettings(items);
    };
  }

  @Bean(BEAN_PREFIX + "loadTargetShopIdsStep")
  @JobScope
  public Step loadTargetShopIdsStep() {
    return new StepBuilder("loadTargetShopIdsStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          List<String> allShopIds = shopQueryRepository.findAllShopIds();

          for (LocalDate operationDate : jobParameter.getOperationDateRange()) {
            List<String> targetShopIds = new ArrayList<>(allShopIds);
            Set<String> existingShopIds = findShopIdsWithOperationInfo(operationDate, allShopIds);

            targetShopIds.removeIf(existingShopIds::contains);

            store.putTargetShopIds(targetShopIds, operationDate);
          }
          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
  }

  @Bean(BEAN_PREFIX + "createOperationInfoStep")
  @JobScope
  public Step createOperationInfoStep() {
    return new StepBuilder("createOperationInfoStep", jobRepository)
        .<OperationTimeSettingsEntity, List<ShopOperationInfoEntity>>
            chunk(chunkSize, platformTransactionManager)
        .reader(createOperationInfoReader())
        .processor(createOperationInfoProcessor())
        .writer(createOperationInfoWriter())
        .build();
  }

  @Bean(BEAN_PREFIX + "createOperationInfoReader")
  @StepScope
  public QuerydslPagingItemReader<OperationTimeSettingsEntity> createOperationInfoReader() {
    return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize,
        queryFactory -> queryFactory
            .selectFrom(operationTimeSettingsEntity)
            .where(
                operationTimeSettingsEntity.isPublished.eq(true),
                operationTimeSettingsEntity.shopId.in(store.getAllTargetShopIds())
            )
    );
  }

  @Bean(BEAN_PREFIX + "createOperationInfoProcessor")
  @StepScope
  public ItemProcessor<OperationTimeSettingsEntity, List<ShopOperationInfoEntity>> createOperationInfoProcessor() {
    return operationTimeSettings -> {
      if (store.hasDuplicateTimeSettings(operationTimeSettings)) {
        return null;
      }

      List<LocalDate> operationDates = store.getTargetShopIdsBy(operationTimeSettings.getShopId());
      return operationDates.stream()
          .map(operationDate -> ShopOperationInitializeFactory.initialize(
              operationTimeSettings,
              store.getRemoteOperationTimeSettings(operationTimeSettings.getShopId()),
              operationDate
          ))
          .toList();
    };
  }

  @Bean(BEAN_PREFIX + "createOperationInfoWriter")
  @StepScope
  public ItemWriter<List<ShopOperationInfoEntity>> createOperationInfoWriter() {
    return chunk -> {
      Map<LocalDate, List<ShopOperationInfoEntity>> groupingEntities = groupByOperationDate(
          chunk.getItems());

      groupingEntities.entrySet()
          .forEach(this::saveByBatch);
    };
  }

  @Bean(BEAN_PREFIX + "slackSendStep")
  @JobScope
  public Step slackSendStep() {
    return new StepBuilder("slackSendStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          // TODO: 2023/02/16 슬랙 전송

          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
  }

  private Set<String> findShopIdsWithOperationInfo(LocalDate operationDate, List<String> shopIds) {
    return shopOperationInfoQueryRepository.findByShopIdsAndOperationDate(
            shopIds, operationDate
        )
        .stream()
        .map(ShopOperationInfoEntity::getShopId)
        .collect(Collectors.toSet());
  }

  private Map<LocalDate, List<ShopOperationInfoEntity>> groupByOperationDate(
      List<? extends List<ShopOperationInfoEntity>> items) {
    List<ShopOperationInfoEntity> processedEntities = items.stream()
        .flatMap(Collection::stream)
        .toList();

    return groupingByList(processedEntities, ShopOperationInfoEntity::getOperationDate);
  }

  private void saveByBatch(Entry<LocalDate, List<ShopOperationInfoEntity>> entry) {
    LocalDate operationDate = entry.getKey();
    List<ShopOperationInfoEntity> entities = entry.getValue();

    List<List<ShopOperationInfoEntity>> partition = ListUtils.partition(entities, BATCH_SIZE);

    for (List<ShopOperationInfoEntity> partitionedEntities : partition) {
      shopOperationInfoService.saveByBatch(partitionedEntities, operationDate);
    }
  }

}
