package co.wadcorp.waiting.gateway.filter;

import co.wadcorp.libs.util.IPUtil;
import co.wadcorp.waiting.gateway.filter.GlobalLoggingFilter.Config;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalLoggingFilter extends AbstractGatewayFilterFactory<Config> {

  private static final String USER_AGENT = "USER-AGENT";
  private static final String DEVICE_ID = "X-Request-ID";

  private static final List<Pattern> EXPORT_API_PATTERN = List.of(
      Pattern.compile("/api/v1/shops/.*/management/waiting"),
      Pattern.compile("/api/v1/shops/.*/management/waiting/settings"),
      Pattern.compile("/api/v1/shops/.*/register/waiting/current-status"),
      Pattern.compile("/api/v1/shops/.*/register/waiting/settings")
  );

  public GlobalLoggingFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {

    return new OrderedGatewayFilter((exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      HttpHeaders headers = request.getHeaders();
      RequestPath path = request.getPath();
      InetSocketAddress remoteAddress = getInetSocketAddress(request, headers);

      // actuator를 스프링에서 알아서 필터링 해주는 중 혹시 몰라서 추가
      if (path.toString().startsWith("/actuator")) {
        return chain.filter(exchange);
      }

      boolean isExportApi = checkExportApi(path);
      if(request.getMethod() == HttpMethod.GET && isExportApi) {
        return chain.filter(exchange);
      }

      log.info(
          "Waiting - Request Path: {}, User Agent: {}, Device Id: {}, IP: {}",
          path,
          headers.getFirst(USER_AGENT),
          headers.getFirst(DEVICE_ID),
          remoteAddress
      );

      return chain.filter(exchange);
    }, Ordered.LOWEST_PRECEDENCE);
  }

  private static boolean checkExportApi(RequestPath path) {
    return EXPORT_API_PATTERN.stream()
        .anyMatch(item -> {
          Matcher matcher = item.matcher(path.toString());
          return matcher.find();
        });
  }

  private static InetSocketAddress getInetSocketAddress(ServerHttpRequest request, HttpHeaders headers) {
    // 1. x-forwarded-header 로부터 IP를 얻거나 request remoteAddr로부터 얻는다.
    String xForwardedFor = headers.getFirst("x-forwarded-for");
    if (StringUtils.isNotBlank(xForwardedFor)) {
      List<InetSocketAddress> addresses = IPUtil.getIpsFromForwarded(xForwardedFor);
      return addresses.get(0);
    }
    return request.getRemoteAddress();
  }

  /**
   * global logger config.
   */
  public static class Config {

  }
}
