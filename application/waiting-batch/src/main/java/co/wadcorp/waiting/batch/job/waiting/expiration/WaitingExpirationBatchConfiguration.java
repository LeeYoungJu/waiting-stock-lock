package co.wadcorp.waiting.batch.job.waiting.expiration;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.waiting.data.domain.waiting.QWaitingEntity.waitingEntity;

import co.wadcorp.waiting.batch.job.waiting.expiration.parameter.WaitingExpirationBatchJobParameter;
import co.wadcorp.waiting.batch.reader.QuerydslZeroPagingItemReader;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingPublisher;
import jakarta.persistence.EntityManagerFactory;
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
 * [웨이팅 만료 상태 변경 배치]
 * <p/>
 * 하루 1번 오전 시간에 수행된다. **전일자로 수행해야 한다는 점 주의!**
 * <p/>
 * 1. Waiting 상태인 웨이팅 조회
 * <p/>
 * 2. EXPIRATION 상태로 변경하여 저장
 * <p/>
 * 3. Slack 결과 전송
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class WaitingExpirationBatchConfiguration {

  public static final String JOB_NAME = "WaitingExpirationBatch";
  private static final String BEAN_PREFIX = JOB_NAME + "_";

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final WaitingExpirationBatchJobParameter jobParameter;
  private final WaitingRepository waitingRepository;
  private final WaitingHistoryRepository waitingHistoryRepository;

  private final WaitingPublisher waitingPublisher;

  @Value("${chunkSize:1000}")
  public int chunkSize;

  @Bean(JOB_NAME)
  public Job job() {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(expireWaitingsStep())
        .next(slackSendStep())
        .build();
  }

  @Bean(BEAN_PREFIX + "expireWaitingsStep")
  @JobScope
  public Step expireWaitingsStep() {
    return new StepBuilder("expireWaitingsStep", jobRepository)
        .<WaitingEntity, WaitingEntity>
            chunk(chunkSize, platformTransactionManager)
        .reader(expireWaitingsReader())
        .processor(expireWaitingsProcessor())
        .writer(expireWaitingsWriter())
        .build();
  }

  @Bean(BEAN_PREFIX + "expireWaitingsReader")
  @StepScope
  public QuerydslZeroPagingItemReader<WaitingEntity> expireWaitingsReader() {
    return new QuerydslZeroPagingItemReader<>(entityManagerFactory, chunkSize,
        queryFactory -> queryFactory
            .selectFrom(waitingEntity)
            .where(
                waitingEntity.waitingStatus.eq(WaitingStatus.WAITING),
                waitingEntity.operationDate.eq(jobParameter.getOperationDate())
            )
    );
  }

  @Bean(BEAN_PREFIX + "expireWaitingsProcessor")
  @StepScope
  public ItemProcessor<WaitingEntity, WaitingEntity> expireWaitingsProcessor() {
    return waitingEntity -> {
      waitingEntity.expire();
      return waitingEntity;
    };
  }

  @Bean(BEAN_PREFIX + "expireWaitingsWriter")
  @StepScope
  public ItemWriter<WaitingEntity> expireWaitingsWriter() {
    return chunk -> {
      List<? extends WaitingEntity> items = chunk.getItems();

      waitingRepository.saveAll(items);
      waitingHistoryRepository.saveAll(convert(items, WaitingHistoryEntity::new));

      waitingPublisher.publish(convert(items, WaitingEntity::getWaitingId));
    };
  }

  @Bean(BEAN_PREFIX + "slackSendStep")
  @JobScope
  public Step slackSendStep() {
    return new StepBuilder("slackSendStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          // TODO: 2023/02/27 슬랙 전송

          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
  }

}
