package co.wadcorp.waiting.batch.job.temp;

import static co.wadcorp.waiting.data.domain.settings.QHomeSettingsEntity.homeSettingsEntity;

import co.wadcorp.waiting.batch.reader.QuerydslPagingItemReader;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * [Json 신규 필드 추가 배치]
 * <p/>
 * 1회성 마이그레이션 용도.
 * <p>
 * 후에 다른 엔티티도 비슷한 변경 사항이 있을 수 있어서 사용 후에도 남겨둔다.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class HomeSettingsJsonFieldAppendBatchConfiguration {

  public static final String JOB_NAME = "HomeSettingsJsonFieldAppendBatch";
  private static final String BEAN_PREFIX = JOB_NAME + "_";

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final EntityManagerFactory entityManagerFactory;

  @Value("${chunkSize:1000}")
  public int chunkSize;

  @Bean(JOB_NAME)
  public Job job() {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(migrationStep())
        .build();
  }

  @Bean(BEAN_PREFIX + "migrationStep")
  @JobScope
  public Step migrationStep() {
    return new StepBuilder("migrationStep", jobRepository)
        .<HomeSettingsEntity, HomeSettingsEntity>
            chunk(chunkSize, platformTransactionManager)
        .reader(migrationReader())
        .processor(migrationProcessor())
        .writer(migrationWriter())
        .build();
  }

  @Bean(BEAN_PREFIX + "migrationReader")
  @StepScope
  public QuerydslPagingItemReader<HomeSettingsEntity> migrationReader() {
    return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize,
        queryFactory -> queryFactory
            .selectFrom(homeSettingsEntity)
    );
  }

  @Bean(BEAN_PREFIX + "migrationProcessor") // 마이그레이션 진행
  @StepScope
  public ItemProcessor<HomeSettingsEntity, HomeSettingsEntity> migrationProcessor() {
    return homeSettingsEntity -> {
//      homeSettingsEntity.migrationPickupToTakeOut();
      return homeSettingsEntity;
    };
  }

  @Bean(BEAN_PREFIX + "migrationWriter")
  @StepScope
  public JpaItemWriter<HomeSettingsEntity> migrationWriter() {
    JpaItemWriter<HomeSettingsEntity> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return jpaItemWriter;
  }

}
