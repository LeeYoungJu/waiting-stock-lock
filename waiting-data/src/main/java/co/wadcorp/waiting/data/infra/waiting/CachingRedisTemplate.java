package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.enums.WaitingModeType;
import java.time.Duration;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CachingRedisTemplate {

  private static final String TABLE_CURRENT_STATUS_KEY = "table-current-status:shop-id:%s:operation-date:%s:mode:%s";
  private static final Duration TABLE_CURRENT_STATUS_TIMEOUT = Duration.ofDays(1);

  private final RedisTemplate<String, String> redisTemplate;

  public CachingRedisTemplate(
      @Qualifier("cacheRedisTemplate") RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public String getTableCurrentStatus(String shopId, LocalDate operationDate,
      WaitingModeType modeType) {
    String key = createTableCurrentStatusKey(shopId, operationDate, modeType);
    try {
      return redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
      log.error("Redis 테이블 현황 조회 실패");
      return null;
    }
  }

  public void setTableCurrentStatus(String shopId, LocalDate operationDate,
      WaitingModeType modeType, String object) {
    String key = createTableCurrentStatusKey(shopId, operationDate, modeType);
    try {
      redisTemplate.opsForValue().set(key, object, TABLE_CURRENT_STATUS_TIMEOUT);
    } catch (Exception e) {
      log.error("Redis 테이블 현황 update 실패");
    }
  }

  private String createTableCurrentStatusKey(String shopId, LocalDate operationDate,
      WaitingModeType modeType) {
    return String.format(TABLE_CURRENT_STATUS_KEY, shopId, operationDate, modeType);
  }

}
