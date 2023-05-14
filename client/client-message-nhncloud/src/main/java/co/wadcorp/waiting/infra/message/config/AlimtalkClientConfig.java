package co.wadcorp.waiting.infra.message.config;

import co.wadcorp.libs.nhn_cloud.alimtalk.AlimtalkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlimtalkClientConfig {

  @Value("${external.nhn-cloud.biz-message.app-key}")
  private String appKey;

  @Value("${external.nhn-cloud.biz-message.secret-key}")
  private String secretKey;

  @Bean
  public AlimtalkClient alimtalkClient() {
    return new AlimtalkClient(appKey, secretKey);
  }
}
