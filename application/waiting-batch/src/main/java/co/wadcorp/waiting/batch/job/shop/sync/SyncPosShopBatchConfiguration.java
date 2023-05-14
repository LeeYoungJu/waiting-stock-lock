package co.wadcorp.waiting.batch.job.shop.sync;

import co.wadcorp.waiting.batch.job.shop.sync.reader.PosShopPagingItemReader;
import co.wadcorp.waiting.batch.job.shop.sync.store.SyncPosShopStore;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.DefaultAlarmSettingsDataFactory;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.DefaultOperationTimeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.DefaultOptionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.DefaultPrecautionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsRepository;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.infra.pos.CatchtablePosShopClient;
import co.wadcorp.waiting.infra.pos.dto.PosSearchShopsResponse.SearchShopInfo;
import co.wadcorp.waiting.shared.util.ListUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * [POS 웨이팅 샵 정보 연동]
 * <p/>
 * 하루 1번 오전 시간에 수행된다.
 * <p/>
 * 1. POS /internal/catchpos/api/v1/shops/search 를 조회해서 매장 정보를 가져온다.
 * <p/>
 * 2. 조회한 매장의 정보를 저장하거나 업데이트한다.
 * <p/>
 * 3. 신규 매장이라면 웨이팅 설정을 기본 값으로 저장한다.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SyncPosShopBatchConfiguration {

  public static final String JOB_NAME = "SyncPosShopBatch";
  private static final String BEAN_PREFIX = JOB_NAME + "_";

  private final CatchtablePosShopClient catchtablePosShopClient;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;

  private final ShopRepository shopRepository;
  private final HomeSettingsRepository homeSettingsRepository;
  private final OperationTimeSettingsRepository operationTimeSettingsRepository;
  private final OptionSettingsRepository optionSettingsRepository;
  private final AlarmSettingsRepository alarmSettingsRepository;
  private final PrecautionSettingsRepository precautionSettingsRepository;

  private final SyncPosShopStore syncPosShopStore;

  @Value("${chunkSize:100}")
  public int chunkSize;

  @Bean(JOB_NAME)
  public Job job() {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(createPosShopStep())
        .next(createWaitingShopStep())
        .next(createSettingStep())
        .next(slackSendStep())
        .preventRestart()
        .build();
  }

  @Bean(BEAN_PREFIX + "createPosShopStep")
  @JobScope
  public Step createPosShopStep() {
    return new StepBuilder("createPosShopStep", jobRepository)
        .<SearchShopInfo, SearchShopInfo>chunk(chunkSize, platformTransactionManager)
        .reader(createPosShopReader())
        .writer(createPosShopWriter())
        .build();
  }

  @Bean(BEAN_PREFIX + "createPosShopReader")
  @StepScope
  public PosShopPagingItemReader createPosShopReader() {
    return new PosShopPagingItemReader(catchtablePosShopClient, chunkSize);
  }

  @Bean(BEAN_PREFIX + "createPosShopWriter")
  @StepScope
  public ItemWriter<SearchShopInfo> createPosShopWriter() {
    return chunk -> syncPosShopStore.addAll(chunk.getItems());
  }

  @Bean(BEAN_PREFIX + "createWaitingShopStep")
  @JobScope
  public Step createWaitingShopStep() {
    return new StepBuilder("createWaitingShopStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          Map<String, SearchShopInfo> shopInfoMap = syncPosShopStore.getShopInfoMap();
          List<String> posShopIds = new ArrayList<>(shopInfoMap.keySet());
          List<List<String>> partition = ListUtils.partition(posShopIds, chunkSize);

          for (List<String> shopIds : partition) {
            saveShopEntities(shopInfoMap, shopIds);
          }

          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
  }

  @Bean(BEAN_PREFIX + "createSettingStep")
  @JobScope
  public Step createSettingStep() {
    return new StepBuilder("createSettingStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          List<String> targetCreateSettingShopId = syncPosShopStore.getTargetCreateSettingShopId();

          List<HomeSettingsEntity> homeSettingsEntities = new ArrayList<>();
          List<OperationTimeSettingsEntity> operationTimeSettingsEntities = new ArrayList<>();
          List<OptionSettingsEntity> optionSettingsEntities = new ArrayList<>();
          List<AlarmSettingsEntity> alarmSettingsEntities = new ArrayList<>();
          List<PrecautionSettingsEntity> precautionSettingsEntities = new ArrayList<>();

          for (String shopId : targetCreateSettingShopId) {
            homeSettingsEntities.add(
                new HomeSettingsEntity(shopId, DefaultHomeSettingDataFactory.create()));
            operationTimeSettingsEntities.add(new OperationTimeSettingsEntity(shopId,
                DefaultOperationTimeSettingDataFactory.create()));
            optionSettingsEntities.add(
                new OptionSettingsEntity(shopId, DefaultOptionSettingDataFactory.create()));
            alarmSettingsEntities.add(
                new AlarmSettingsEntity(shopId, DefaultAlarmSettingsDataFactory.create()));
            precautionSettingsEntities.add(
                new PrecautionSettingsEntity(shopId, DefaultPrecautionSettingDataFactory.create()));
          }

          homeSettingsRepository.saveAll(homeSettingsEntities);
          operationTimeSettingsRepository.saveAll(operationTimeSettingsEntities);
          optionSettingsRepository.saveAll(optionSettingsEntities);
          alarmSettingsRepository.saveAll(alarmSettingsEntities);
          precautionSettingsRepository.saveAll(precautionSettingsEntities);

          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
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

  private void saveShopEntities(Map<String, SearchShopInfo> shopInfoMap, List<String> shopIds) {
    List<ShopEntity> shopEntities = shopRepository.findAllByShopIdIn(shopIds);
    Map<String, ShopEntity> shopEntityMap = shopEntities.stream()
        .collect(Collectors.toMap(ShopEntity::getShopId, item -> item));

    List<ShopEntity> newShopEntities = new ArrayList<>();
    for (String shopId : shopIds) {
      SearchShopInfo shopInfo = shopInfoMap.get(shopId);

      Optional.ofNullable(shopEntityMap.get(shopId))
          .ifPresentOrElse(
              entity -> updateShopInfo(shopInfo, entity),
              () -> {
                newShopEntities.add(createShopEntity(shopId, shopInfo));
                syncPosShopStore.addTargetCreateSettingShopId(shopId);
              }
          );
    }
    shopRepository.saveAll(newShopEntities);
  }

  private void updateShopInfo(SearchShopInfo shopInfo, ShopEntity entity) {
    String shopName = shopInfo.getShopName();
    String shopAddress = shopInfo.getShopAddress();

    if (entity.hasShopTelNumber()) {
      entity.update(shopName, shopAddress);
      return;
    }
    entity.update(shopName, shopAddress, shopInfo.getShopTelNumber());
  }

  private static ShopEntity createShopEntity(String shopId, SearchShopInfo shopInfo) {
    return ShopEntity.builder()
        .shopId(shopId)
        .shopName(shopInfo.getBasicInfo().getShopName())
        .shopAddress(shopInfo.getShopAddress())
        .shopTelNumber(shopInfo.getShopTelNumber())
        .isUsedRemoteWaiting(true)
        .isTest(false) // TODO: 2023/03/06 나중에 포스 응답 추가되면 변경 필요
        .isMembership(true) // TODO: 2023/03/08 나중에 포스 응답 추가되면 변경 필요
        .build();
  }

}
