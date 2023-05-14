package co.wadcorp.waiting.api.config;

import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WEB, WEB MVC 등과 관련한 설정.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final ChannelShopIdArgumentResolver channelShopIdArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(channelShopIdArgumentResolver);
  }
}
