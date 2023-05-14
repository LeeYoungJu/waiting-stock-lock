package co.wadcorp.waiting.gateway.filter;

import co.wadcorp.libs.util.IPUtil;
import co.wadcorp.waiting.gateway.auth.CidrRangeChecker;
import co.wadcorp.waiting.gateway.config.GatewayProps;
import co.wadcorp.waiting.gateway.filter.InternalAccessOnlyFilter.Config;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

/**
 * 내부 네트워크 대역에서만 액세스를 허용한다.
 */
@Slf4j
@Component
public class InternalAccessOnlyFilter extends AbstractGatewayFilterFactory<Config> {

  @Resource
  private GatewayProps gatewayProps;

  private CidrRangeChecker cidrRangeChecker;

  public InternalAccessOnlyFilter() {
    super(Config.class);
  }

  @PostConstruct
  public void init() {
    cidrRangeChecker = new CidrRangeChecker(gatewayProps.getInternalCidrs());
  }

  @Override
  public GatewayFilter apply(InternalAccessOnlyFilter.Config config) {
    return (exchange, chain) -> {
      final ServerHttpRequest request = exchange.getRequest();
      final ServerHttpResponse response = exchange.getResponse();
      final HttpHeaders headers = request.getHeaders();

      InetSocketAddress remoteAddress;

      // 1. x-forwarded-header 로부터 IP를 얻거나 request remoteAddr로부터 얻는다.
      String xForwardedFor = headers.getFirst("x-forwarded-for");
      if (StringUtils.isNotBlank(xForwardedFor)) {
        List<InetSocketAddress> addresses = IPUtil.getIpsFromForwarded(xForwardedFor);
        remoteAddress = addresses.get(0);
      } else {
        remoteAddress = request.getRemoteAddress();
      }

      if (cidrRangeChecker.isInRange(remoteAddress)) {
        return chain.filter(exchange);
      }

      log.warn("disallowed internal access ip:{}, xForwardedFor:{}", remoteAddress, xForwardedFor);

      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return response.setComplete();
    };
  }

  /**
   * InternalAccessOnlyFilter config.
   */
  public static class Config {

  }
}
