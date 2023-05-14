package co.wadcorp.waiting.handler.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "waiting.web")
public class WaitingWebProperties {

  private String waitingUrl;
  private String restoreUrl;
}