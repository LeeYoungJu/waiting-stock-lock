package co.wadcorp.waiting.gateway.filter;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 정적 응답 헤더 필터.
 */
@Component
public class StaticResponseHeaderGatewayFilterFactory extends
    AbstractGatewayFilterFactory<StaticResponseHeaderGatewayFilterFactory.Config> {

  public StaticResponseHeaderGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerWebExchangeUtils.setResponseStatus(exchange, config.getHttpStatus());
      ServerWebExchangeUtils.setAlreadyRouted(exchange);
      return chain.filter(exchange.mutate().build());
    };
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return List.of("httpStatus");
  }

  /**
   * gateway config.
   */
  @Getter
  @Setter
  public static class Config {

    private HttpStatus httpStatus;

    public HttpStatus getHttpStatus() {
      return this.httpStatus;
    }

    /**
     * httpStatusCode는 숫자 형식이다.
     *
     * @param httpStatusCode 200, 404, ... 등
     */
    public void setHttpStatus(String httpStatusCode) {
      this.httpStatus = HttpStatus.valueOf(Integer.parseInt(httpStatusCode));
    }
  }
}
