package co.wadcorp.waiting.gateway.auth.authenticator;

import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 채널링 인증을 수행한다.
 */
public interface ChannelAuthenticator {

  /**
   * 이 ChannelAuthenticator의 CHANNEL ID를 반환한다. X-CHANNEL-ID 헤더에 설정된다.
   *
   * @return 채널ID. 예)CATCHTABLE-B2C, CATCH-WAITING
   */
  ServiceChannelId getServiceChannelId();

  /**
   * 주어진 HttpRequest를 가지고 이 ChannelAuthenticator가 처리해야 하는 것인지를 결정한다.
   *
   * @param request 현재 요청.
   * @return 이 ChannelAuthenticator 가 처리해야 하는 요청이면 true를 반환.
   */
  boolean isMatch(ServerHttpRequest request);

  /**
   * 인증 처리를 수행한다. 필요한 경우, 인증 관련 처리가 적용된 ServerWebExchange를 반환해야 한다.
   *
   * @return 인증 처리된 ServerWebExchange를 반환한다.
   */
  Mono<ServerWebExchange> authenticate(ServerWebExchange originalExchange);

}
