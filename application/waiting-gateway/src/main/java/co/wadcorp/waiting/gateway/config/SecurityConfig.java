package co.wadcorp.waiting.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * api-gateway의 주요 리소스에 접근을 제어하기 위한 보안 설정을 추가한 클래스.
 */
@Configuration
public class SecurityConfig {

  /**
   * 기본 filter chain. actuator url에 인증을 설정하는 것이 주목적이다.
   */
  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity serverHttpSecurity) {
    return serverHttpSecurity.authorizeExchange()
        .pathMatchers("/actuator/health").permitAll()
        .pathMatchers("/api/v1/login").permitAll()
        .pathMatchers("/actuator/**").authenticated()
        .anyExchange().permitAll()
        .and()
        .httpBasic()
        .and()
        .cors().and() // 임시
        .csrf().disable()
        .formLogin().disable()
        .build();
  }

}

