package co.wadcorp.waiting.batch.job.shop.remote;


import co.wadcorp.waiting.batch.job.shop.remote.parameter.RemoteShopTriggerBatchJobParameter;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.infra.channel.JpaChannelMappingRepository;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingShopOperationPublisher;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * [B2C 매장 정보 카프카 트리거]
 * <p/>
 * 1. 웨이팅 파라미터에서 shopSeq를 읽어온다. (여러개 입력을 하려면 구분자로 `:`을 사용한다.)
 * <p/>
 * 2. b2b-waiting-shopOpeartion 토픽에 이벤트를 호출한다.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RemoteShopTriggerBatchConfiguration {

  public static final String JOB_NAME = "RemoteShopTriggerBatch";
  private static final String BEAN_PREFIX = JOB_NAME + "_";

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final RemoteShopTriggerBatchJobParameter jobParameter;

  private final JpaChannelMappingRepository jpaChannelMappingRepository;
  private final WaitingShopOperationPublisher waitingShopOperationPublisher;


  @Bean(JOB_NAME)
  public Job job() {
    return new JobBuilder(JOB_NAME, jobRepository)
        .start(remoteShopTriggerStep())
        .preventRestart()
        .build();
  }


  @Bean(BEAN_PREFIX + "remoteShopTriggerStep")
  @JobScope
  public Step remoteShopTriggerStep() {
    return new StepBuilder("remoteShopTriggerStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {

          LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
          List<String> shopSeqs = jobParameter.getShopSeq();

          List<ChannelMappingEntity> channelMappingEntities = jpaChannelMappingRepository.findByChannelShopIds(
              ServiceChannelId.CATCHTABLE_B2C.getValue(),
              shopSeqs
          );

          for (ChannelMappingEntity channelMappingEntity : channelMappingEntities) {
            log.info("매장 운영정보 변경 이벤트 shopSeq={}, operationDate={}",
                channelMappingEntity.getChannelShopId(), operationDate);

            waitingShopOperationPublisher.publish(
                Long.valueOf(channelMappingEntity.getChannelShopId()),
                operationDate
            );
          }

          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
  }
}
