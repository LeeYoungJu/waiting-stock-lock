package co.wadcorp.waiting.data.infra.waiting;

import java.time.Duration;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class DistributedLockRedisTemplate {

  private static final Duration PHONE_NUMBER_TIMEOUT = Duration.ofSeconds(5);

  private static final String WAITING_NUMBER_KEY = "waiting-number:shop-id:%s:operation-date:%s";
  private static final String WAITING_ORDER_KEY = "waiting-order:shop-id:%s:operation-date:%s";
  private static final String CUSTOMER_PHONE_KEY = "customer-phone:shop-id:%s:operation-date:%s:number:%s";

  private final RedisTemplate<String, Integer> waitingNumberRedisTemplate;
  private final RedisTemplate<String, String> customerPhoneRedisTemplate;

  public DistributedLockRedisTemplate(
      @Qualifier("waitingNumberRedisTemplate") RedisTemplate<String, Integer> waitingNumberRedisTemplate,
      @Qualifier("customerPhoneRedisTemplate") RedisTemplate<String, String> customerPhoneRedisTemplate
  ) {
    this.waitingNumberRedisTemplate = waitingNumberRedisTemplate;
    this.customerPhoneRedisTemplate = customerPhoneRedisTemplate;
  }

  public Integer getWaitingOrder(String shopId, LocalDate operationDate) {
    ValueOperations<String, Integer> valueOps = waitingNumberRedisTemplate.opsForValue();

    return valueOps.get(String.format(WAITING_ORDER_KEY, shopId, operationDate));
  }

  public Long incrementWaitingOrder(String shopId, LocalDate operationDate) {
    ValueOperations<String, Integer> valueOps = waitingNumberRedisTemplate.opsForValue();

    return valueOps.increment(String.format(WAITING_ORDER_KEY, shopId, operationDate));
  }

  public Long incrementWaitingNumber(String shopId, LocalDate operationDate) {
    ValueOperations<String, Integer> valueOps = waitingNumberRedisTemplate.opsForValue();

    return valueOps.increment(String.format(WAITING_NUMBER_KEY, shopId, operationDate));
  }

  public Boolean setPhoneNumberIfAbsent(String shopId, LocalDate operationDate,
      String encryptedPhoneNumber) {
    String customerPhoneKey = String.format(CUSTOMER_PHONE_KEY, shopId, operationDate,
        encryptedPhoneNumber);

    ValueOperations<String, String> valueOps = customerPhoneRedisTemplate.opsForValue();
    Boolean result = valueOps.setIfAbsent(customerPhoneKey, encryptedPhoneNumber,
        PHONE_NUMBER_TIMEOUT);
    return result == null || !result;
  }

}
