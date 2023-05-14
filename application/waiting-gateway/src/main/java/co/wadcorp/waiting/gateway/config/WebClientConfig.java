package co.wadcorp.waiting.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 와 관련한 설정을 정의한다.
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final GatewayProps gatewayProps;

  /**
   * 기본 WebClient 를 생성한다.
   *
   * <p>나중에는 E-Tag 캐시를 지원하기 위해 WebClient의 내부 connector를 HttpComponent 로 변경하는 것을 고려해야 한다.
   */
  @Bean
  public WebClient defaultWebClient() {
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
        .codecs(configurer -> {
          configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
          configurer.defaultCodecs().enableLoggingRequestDetails(
              gatewayProps.isEnableLoggingRequestDetails());
        })
        .build();
    WebClient webClient = WebClient.builder()
        .exchangeStrategies(exchangeStrategies)
        .build();
    return webClient;
  }

}
