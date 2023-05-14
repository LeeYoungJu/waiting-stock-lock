package co.wadcorp.waiting.infra.pos;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
class CatchtablePosClientConfig {

  private final CatchtablePosClientProperties posClientProperties;

  @Bean
  @ConditionalOnMissingBean
  WebClient posClient() {
    return WebClient.builder()
        .baseUrl(posClientProperties.getHost())
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  CatchtablePosLoginHttpClient posLoginHttpClient(WebClient posClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory.builder(WebClientAdapter.forClient(posClient))
            .build();

    return httpServiceProxyFactory.createClient(CatchtablePosLoginHttpClient.class);
  }


  @Bean
  @ConditionalOnMissingBean
  CatchtablePosShopHttpClient posShopHttpClient(WebClient posClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory.builder(WebClientAdapter.forClient(posClient))
            .build();

    return httpServiceProxyFactory.createClient(CatchtablePosShopHttpClient.class);
  }

  @Bean
  @ConditionalOnMissingBean
  CatchtablePosUserHttpClient posUserHttpClient(WebClient posClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory.builder(WebClientAdapter.forClient(posClient))
            .build();

    return httpServiceProxyFactory.createClient(CatchtablePosUserHttpClient.class);
  }
}
