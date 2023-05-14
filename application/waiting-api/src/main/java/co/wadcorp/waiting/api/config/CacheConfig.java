package co.wadcorp.waiting.api.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * waiting-api 에서 사용하는 캐시 관련 설정을 정의한다.
 */
@Configuration
public class CacheConfig {

  /**
   * 캐치테이블 B2C 의 shopSeq와 웨이팅의 shopId를 저장하는 캐시.
   */
  @Bean
  public Cache<String, String> b2cChannelShopIdCache() {
    return Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(3000)
        .build();
  }

}
