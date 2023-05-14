package co.wadcorp.waiting.websocket.auth;

/**
 * 토큰이 유효하지 않을 때 발생하는 예외.
 */
public class InvalidAuthenticationException extends RuntimeException {

  public InvalidAuthenticationException() {
    super();
  }

  public InvalidAuthenticationException(String message) {
    super(message);
  }
}
