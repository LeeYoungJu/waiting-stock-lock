package co.wadcorp.waiting.data.config;

import co.wadcorp.libs.vault.AwsSecretManager;
import co.wadcorp.libs.vault.response.VaultService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

@Configuration
public class BeanConfig {

  @Resource
  private Environment environment;

  /***
   * AWS Secret Manager 인스턴스
   */
  @Bean
  @DependsOn("awsSecretAutoSetter")
  public VaultService awsSecretManager() {
    String secretName;
    Set<String> activeProfiles = new HashSet<>(List.of(environment.getActiveProfiles()));
    if (activeProfiles.isEmpty()) { // 로컬용
      secretName = AwsSecretManager.SECRET_NAME_ALPHA;
    } else if (activeProfiles.contains("real")) {
      secretName = AwsSecretManager.SECRET_NAME_REAL;
    } else if (activeProfiles.contains("test")) {
      return vaultKey -> "testKeyOf16byte!";
    } else {
      secretName = AwsSecretManager.SECRET_NAME_ALPHA;
    }

    AwsSecretManager awsSecretManager = new AwsSecretManager();
    awsSecretManager.initialize(secretName);
    return awsSecretManager;
  }
}
