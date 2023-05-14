package co.wadcorp.waiting;

import co.wadcorp.waiting.data.infra.waiting.CachingRedisTemplate;
import co.wadcorp.waiting.data.infra.waiting.DistributedLockRedisTemplate;
import co.wadcorp.waiting.data.support.PhoneNumberConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

/**
 * 데이터 클렌징 정책은 @Transactional 롤백을 사용한다.
 * <p>
 * 트랜잭션 경계 내의 변경 감지 동작, 다수 트랜잭션 참여 등의 작업에서 부작용이 있을 수 있음을 인지하고 사용
 * <a href="https://www.inflearn.com/questions/792383/테스트에서의-transactional-사용에-대해-질문이-있습니다">...</a>
 * <p>
 * 커스텀한 트랜잭션 경계 설정이 있을 수 있기 때문에 `IntegrationTest`가 아닌 하위 클래스에 `@Transactional`을 붙인다.
 */
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTest {

  @MockBean
  protected PhoneNumberConverter phoneNumberConverter;

  @MockBean
  protected CachingRedisTemplate cachingRedisTemplate;

  @MockBean
  protected DistributedLockRedisTemplate distributedLockRedisTemplate;
  
}
