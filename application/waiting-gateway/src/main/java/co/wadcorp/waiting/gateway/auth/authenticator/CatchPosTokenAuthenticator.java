package co.wadcorp.waiting.gateway.auth.authenticator;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import co.wadcorp.waiting.gateway.auth.InvalidAuthenticationException;
import co.wadcorp.waiting.gateway.auth.model.CatchPosTokenVerifyResponse;
import co.wadcorp.waiting.gateway.config.GatewayProps;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * 캐치테이블 포스용 인증 유효성을 검사하는 클래스.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatchPosTokenAuthenticator implements ChannelAuthenticator {

  private final Cache<String, CatchPosTokenVerifyResponse> catchPosTokenVerifyResponseCache;
  private final WebClient defaultWebClient;
  private final GatewayProps gatewayProps;

  @Override
  public ServiceChannelId getServiceChannelId() {
    return ServiceChannelId.CATCH_WAITING;
  }

  @Override
  public boolean isMatch(ServerHttpRequest request) {
    String catchPosToken = request.getHeaders().getFirst("X-CTM-AUTH");
    return StringUtils.isNotBlank(catchPosToken);
  }

  @Override
  public Mono<ServerWebExchange> authenticate(ServerWebExchange originalExchange) {
    final ServerHttpRequest request = originalExchange.getRequest();
    final HttpHeaders headers = request.getHeaders();

    String catchPosToken = headers.getFirst("X-CTM-AUTH");
    Mono<CatchPosTokenVerifyResponse> result = fetchCatchPosTokenVerifyResponse(catchPosToken);

    return result
        .doOnNext(verifyResponse -> {
          if (!verifyResponse.isValid()) {
            throw new InvalidAuthenticationException("failed to validate token");
          }
        })
        .flatMap(verityResponse -> {
          ServerHttpRequest modifiedRequest = originalExchange.getRequest().mutate()
              .header("X-GW-POS-USER-SEQ", String.valueOf(verityResponse.getUserSeq()))
              .header("X-GW-POS-USER-TYPE", verityResponse.getUserType())
              .build();
          ServerWebExchange modifiedExchange = originalExchange.mutate()
              .request(modifiedRequest)
              .build();
          return Mono.just(modifiedExchange);
        });
  }

  /**
   * 토큰 검증 결과를 가져온다.
   *
   * @param token X-CTM-AUTH 헤더를 통해 전달된 토큰.
   */
  private Mono<CatchPosTokenVerifyResponse> fetchCatchPosTokenVerifyResponse(String token) {
    Mono<CatchPosTokenVerifyResponse> result;
    var cached = catchPosTokenVerifyResponseCache.getIfPresent(token);
    if (cached != null) {
      return Mono.just(cached);
    }

    UriComponents uriComponents = UriComponentsBuilder
        .fromHttpUrl(gatewayProps.getCatchTablePosApiHost())
        .path("/oauth/api/validate")
        .queryParam("token", token)
        .build();
    result = defaultWebClient.get()
        .uri(uriComponents.toUri())
        .header("Content-Type", "application/json; charset=utf-8")
        .retrieve()
        // 포스에서 401 에러코드를 준다면 인증 실패로 전달한다.
        .onStatus(status -> status == UNAUTHORIZED,
            clientResponse -> clientResponse.createException()
                .flatMap(it -> Mono.error(
                    new InvalidAuthenticationException("failed to validate token"))))
        // 주의: catchpos의 /oauth/api/validate는 토큰이 유효하지 않은 경우라도 200 OK를 리턴한다.
        // 아래 코드는 토큰이 무효한 경우가 아닌 다른 4xx, 5xx 에러를 처리하기 위해 예외를 발생시키는 코드이다.
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
            ClientResponse::createError
        )
        .bodyToMono(CatchPosTokenVerifyResponse.class)
        .doOnNext(verifyResponse -> catchPosTokenVerifyResponseCache.put(token, verifyResponse));
    return result;
  }
}
