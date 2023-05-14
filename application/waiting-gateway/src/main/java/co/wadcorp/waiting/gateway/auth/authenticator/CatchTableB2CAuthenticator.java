package co.wadcorp.waiting.gateway.auth.authenticator;

import co.wadcorp.libs.util.IPUtil;
import co.wadcorp.waiting.gateway.auth.CidrRangeChecker;
import co.wadcorp.waiting.gateway.auth.InvalidAuthenticationException;
import co.wadcorp.waiting.gateway.config.GatewayProps;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import jakarta.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * CatchTable B2C 인증 유효성 검사. CatchTable B2C 호출은 내부 호출만 허용한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatchTableB2CAuthenticator implements ChannelAuthenticator {

  private final GatewayProps gatewayProps;

  private CidrRangeChecker cidrRangeChecker;

  @PostConstruct
  public void init() {
    cidrRangeChecker = new CidrRangeChecker(gatewayProps.getInternalCidrs());
  }

  @Override
  public ServiceChannelId getServiceChannelId() {
    return ServiceChannelId.CATCHTABLE_B2C;
  }

  @Override
  public boolean isMatch(ServerHttpRequest request) {
    String channelId = request.getHeaders().getFirst("X-CHANNEL-ID");
    return ServiceChannelId.CATCHTABLE_B2C.getValue().equals(channelId);
  }

  /**
   * B2C로부터의 호출은 내부로부터의 접속만 허용한다.
   */
  @Override
  public Mono<ServerWebExchange> authenticate(ServerWebExchange originalExchange) {
    final ServerHttpRequest request = originalExchange.getRequest();

    InetSocketAddress remoteAddress;
    // 1. x-forwarded-header 로부터 IP를 얻거나 request remoteAddr로부터 얻는다.
    String xForwardedFor = request.getHeaders().getFirst("x-forwarded-for");
    if (StringUtils.isNotBlank(xForwardedFor)) {
      List<InetSocketAddress> addresses = IPUtil.getIpsFromForwarded(xForwardedFor);
      remoteAddress = addresses.get(0);
    } else {
      remoteAddress = request.getRemoteAddress();
    }

    if (!cidrRangeChecker.isInRange(remoteAddress)) {
      throw new InvalidAuthenticationException("failed to authenticate b2c network range.");
    }

    return Mono.just(originalExchange);
  }
}
