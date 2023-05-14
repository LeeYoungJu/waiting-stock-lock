package co.wadcorp.waiting.gateway.filter;

import co.wadcorp.waiting.gateway.auth.InvalidAuthenticationException;
import co.wadcorp.waiting.gateway.auth.authenticator.ChannelAuthenticator;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 캐치웨이팅 인증 처리 기본 필터.
 *
 * <p>기본적으로 CatchPos 토큰이면 CatchPos 인증을 태우고,
 * 그렇지 않고 다른 채널(예: CatchTable-B2C) 요청이면 해당 채널로 인증을 처리한다.
 */
@Slf4j
@Component
public class CatchWaitingAuthFilter extends
    AbstractGatewayFilterFactory<CatchWaitingAuthFilter.Config> {

  @Resource
  private List<ChannelAuthenticator> authenticators;

  public CatchWaitingAuthFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      final ServerHttpRequest request = exchange.getRequest();
      final HttpHeaders headers = request.getHeaders();

      // 채널 매칭 검사
      Optional<ChannelAuthenticator> optChannelAuthenticator = authenticators.stream()
          .filter(channelAuthenticator -> channelAuthenticator.isMatch(request))
          .findFirst();
      if (optChannelAuthenticator.isEmpty()) {
        log.warn("Unknown authentication. headers:{}", headers);
        throw new InvalidAuthenticationException("UNKNOWN AUTHENTICATION");
      }

      // 인증 처리 수행
      ChannelAuthenticator authenticator = optChannelAuthenticator.get();
      Mono<ServerWebExchange> result = authenticator.authenticate(exchange);
      return result.flatMap(authedExchange -> {
        ServerHttpRequest modifiedRequest = authedExchange.getRequest().mutate()
            .header("X-GW-CHANNEL-ID", authenticator.getServiceChannelId().getValue())
            .build();
        ServerWebExchange modifiedExchange = authedExchange.mutate().request(modifiedRequest)
            .build();
        return chain.filter(modifiedExchange);
      });
    };
  }

  /**
   * gateway config.
   */
  public static class Config {

  }
}
