package co.wadcorp.waiting.api;

import co.wadcorp.waiting.data.infra.waiting.CachingRedisTemplate;
import co.wadcorp.waiting.data.infra.waiting.DistributedLockRedisTemplate;
import co.wadcorp.waiting.data.service.waiting.WaitingNumberService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTest {

  @MockBean
  protected WaitingNumberService waitingNumberService;

  @MockBean
  protected CachingRedisTemplate cachingRedisTemplate;

  @MockBean
  protected DistributedLockRedisTemplate distributedLockRedisTemplate;

}
