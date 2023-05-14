package co.wadcorp.waiting.batch.job;

import co.wadcorp.waiting.data.infra.waiting.CachingRedisTemplate;
import co.wadcorp.waiting.data.infra.waiting.DistributedLockRedisTemplate;
import co.wadcorp.waiting.data.service.waiting.WaitingNumberService;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingPublisher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest
public abstract class BatchTestSupport {

  @Autowired
  protected JobLauncherTestUtils jobLauncherTestUtils;

  @MockBean
  private WaitingPublisher waitingPublisher;

  @MockBean
  protected WaitingNumberService waitingNumberService;

  @MockBean
  protected CachingRedisTemplate cachingRedisTemplate;

  @MockBean
  protected DistributedLockRedisTemplate distributedLockRedisTemplate;

}
