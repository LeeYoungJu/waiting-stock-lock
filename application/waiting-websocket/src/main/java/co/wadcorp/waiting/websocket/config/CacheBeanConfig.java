package co.wadcorp.waiting.websocket.config;

import co.wadcorp.waiting.websocket.auth.model.CatchPosTokenVerifyResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 캐시와 관련한 bean 들을 정의한다.
 */
@Configuration
public class CacheBeanConfig {

  /**
   * 캐치테이블 포스용 인증 유효성 검사 결과 캐시. 다른 채널의 토큰은 저장하지 않음.
   */
  @Bean
  public Cache<String, CatchPosTokenVerifyResponse> catchPosTokenVerifyResponseCache() {
    return Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .maximumSize(500)
        .build();
  }

}
