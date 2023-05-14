package co.wadcorp.waiting.data.exception;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AppException extends RuntimeException {

  private String code;
  private String displayMessage;
  private Object data;

  public AppException() {
  }

  public AppException(HttpStatus httpStatus) {
    setCode(httpStatus);
  }

  public AppException(HttpStatus httpStatus, final ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.displayMessage = errorCode.getMessage();
    this.data = Map.of("reason", errorCode.getCode());
    setCode(httpStatus);
  }

  // 외부 API 호출할때 전달받은 코드를 그대로 사용하기 위함
  public AppException(HttpStatus httpStatus, final String message, final String displayMessage,
      final String code) {
    super(message);
    this.displayMessage = displayMessage;
    this.data = Map.of("reason", code);
    setCode(httpStatus);
  }

  public AppException(HttpStatus httpStatus, final String displayMessage) {
    super(displayMessage);
    this.displayMessage = displayMessage;
    setCode(httpStatus);
  }

  public AppException(HttpStatus httpStatus, final String message, final String displayMessage) {
    super(message);
    this.displayMessage = displayMessage;
    setCode(httpStatus);
  }

  public AppException(HttpStatus httpStatus, final String displayMessage, Throwable cause) {
    super(displayMessage, cause);
    this.displayMessage = displayMessage;
    setCode(httpStatus);
  }

  public AppException(HttpStatus httpStatus, final String message, final String displayMessage,
      Throwable cause) {
    super(message, cause);
    this.displayMessage = displayMessage;
    setCode(httpStatus);
  }


  public AppException(HttpStatus httpStatus, final String message, final String displayMessage,
      final Object data) {
    super(message);
    this.displayMessage = displayMessage;
    this.data = data;
    setCode(httpStatus);
  }

  public static AppException ofBadRequest(String message) {
    return new AppException(HttpStatus.BAD_REQUEST, message);
  }

  public static AppException ofBadRequest(ErrorCode errorCode) {
    return new AppException(HttpStatus.BAD_REQUEST, errorCode);
  }

  public void setCode(HttpStatus httpStatus) {
    this.code = String.valueOf(httpStatus.value());
  }

}
