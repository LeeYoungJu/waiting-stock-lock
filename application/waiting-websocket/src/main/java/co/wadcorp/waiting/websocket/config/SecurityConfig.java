package co.wadcorp.waiting.websocket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        // CORS semantic 상으로 CORS prefight에는 Authorization 헤더를 줄 이유가 없으므로 CORS preflight 요청에 대해서는 401 응답을 하면 안된다.
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        // 그외 요청은 모두 허용
        .anyRequest().permitAll()
        .and()
        .cors()// Origin  헤더가 있는 모든 요청에 대해 CORS 헤더를 포함한 응답을 해준다.
        .and()
        .formLogin().disable();
    return http.build();
  }
}
