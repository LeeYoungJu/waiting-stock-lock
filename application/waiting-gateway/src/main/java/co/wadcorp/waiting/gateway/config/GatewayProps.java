package co.wadcorp.waiting.gateway.config;

import co.wadcorp.libs.json.JsonUtil;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * gateway 사용자 정의 props.
 */
@Configuration
@ConfigurationProperties(prefix = "gateway-props")
@Getter
@Setter
@Slf4j
public class GatewayProps {

  /**
   * 캐치테이블포스 API 호스트. 예) https://apigw.catchpos.co.kr
   */
  private String catchTablePosApiHost;

  /**
   * WebClient 로 수행하는 요청에 대해 상세하게 로그를 남길지 여부.
   */
  private boolean isEnableLoggingRequestDetails;

  /**
   * 내부 IP 대역.
   */
  private Set<String> internalCidrs;

  @PostConstruct
  public void init() {
    log.info("gateway-props: {}", JsonUtil.getJson(this));
  }
}
