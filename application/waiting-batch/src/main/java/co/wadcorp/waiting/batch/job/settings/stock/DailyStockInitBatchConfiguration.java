package co.wadcorp.waiting.batch.job.settings.stock;

import static co.wadcorp.waiting.data.domain.menu.QMenuEntity.menuEntity;

import co.wadcorp.waiting.batch.job.settings.stock.parameter.DailyStockInitBatchJobParameter;
import co.wadcorp.waiting.batch.reader.QuerydslPagingItemReader;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.service.menu.MenuService;
import co.wadcorp.waiting.data.service.stock.StockService;
import co.wadcorp.waiting.shared.util.ListUtils;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
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
 * [일별 메뉴 재고 데이터 생성 배치]
 * <p/>
 * 하루 1번 오전 시간에 수행된다.
 * <p/>
 * 1. MenuEntity 조회하여 used_daily_stock_yn, used_daily_stock_yn를 확인해서 stock 데이터 추출
 * <p/>
 * 2. 추출한 데이터로 StockEntity 초기화 생성해서 저장
 * <p/>
 * 3. Slack 결과 전송 (TODO)
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DailyStockInitBatchConfiguration {

  public static final String JOB_NAME = "DailyStockInitBatch";
  private static final String BEAN_PREFIX = JOB_NAME + "_";

  private static final int BATCH_SIZE = 100;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final DailyStockInitBatchJobParameter jobParameter;
  private final MenuService menuService;
  private final StockService stockService;

  @Value("${chunkSize:1000}")
  public int chunkSize;

  @Bean(JOB_NAME)
  public Job job() {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(createDailyStockStep())
        .next(slackSendStep())
        .build();
  }

  @Bean(BEAN_PREFIX + "createDailyStockStep")
  @JobScope
  public Step createDailyStockStep() {
    return new StepBuilder("createOperationInfoStep", jobRepository)
        .<MenuEntity, StockEntity>
            chunk(chunkSize, platformTransactionManager)
        .reader(createDailyStockReader())
        .processor(createDailyStockProcessor())
        .writer(createDailyStockWriter())
        .build();
  }

  @Bean(BEAN_PREFIX + "createDailyStockReader")
  @StepScope
  public QuerydslPagingItemReader<MenuEntity> createDailyStockReader() {
    return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize,
        queryFactory -> queryFactory
            .selectFrom(menuEntity)
            .where(
                menuEntity.isDeleted.eq(false)
            )
    );
  }

  @Bean(BEAN_PREFIX + "createDailyStockProcessor")
  @StepScope
  public ItemProcessor<MenuEntity, StockEntity> createDailyStockProcessor() {
    return menuEntity -> StockEntity.of(menuEntity, jobParameter.getOperationDate());
  }

  @Bean(BEAN_PREFIX + "createDailyStockWriter")
  @StepScope
  public ItemWriter<StockEntity> createDailyStockWriter() {
    return chunk -> {
      List<StockEntity> items = new ArrayList<>(chunk.getItems());
      List<List<StockEntity>> partition = ListUtils.partition(items, BATCH_SIZE);

      for (List<StockEntity> entities : partition) {
        stockService.saveByBatch(entities, jobParameter.getOperationDate());
      }
    };
  }

  @Bean(BEAN_PREFIX + "slackSendStep")
  @JobScope
  public Step slackSendStep() {
    return new StepBuilder("slackSendStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          // TODO: 슬랙 전송

          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
  }
}
