package co.wadcorp.waiting.gateway.filter;

import co.wadcorp.waiting.gateway.auth.InvalidAuthenticationException;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    ServerHttpResponse response = exchange.getResponse();

    if (ex instanceof InvalidAuthenticationException) {
      response.setStatusCode(HttpStatus.UNAUTHORIZED);

      // TODO: rfc7807에 맞게 응답을 구성할 것인지 고민.
      DataBuffer wrap = response.bufferFactory().wrap(ex.getMessage().getBytes());
      return response.writeWith(Flux.just(wrap));
    }

    // General Unhandled Exception
    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    log.error("Unhandled Exception", ex);
    Sentry.captureException(ex);

    return response.setComplete();
  }
}
