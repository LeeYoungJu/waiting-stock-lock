package co.wadcorp.waiting.infra.pos.dto;

import java.io.Serializable;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class PosApiResponse<T> implements Serializable {

  /**
   * 응답 코드.
   */
  private String resultCode;

  /**
   * displayMessage의 의도는, 이용자에게 노출 가능한 메시지를 담아두려는 것이다. 이용자에게 노출해야 할 문구가 있다면 displayMessage에 저장하도록 한다.
   */
  private String displayMessage;

  private String message;
  private String reason;

  private T data;

  public static <T> PosApiResponse<T> failed(Exception e) {
    PosApiResponse<T> response = new PosApiResponse<>();
    response.reason = "알 수 없는 오류가 발생했습니다.";
    return response;

  }

  public static <T> PosApiResponse<T> failed(PosApiResponse<Reason> failedResponse) {
    PosApiResponse<T> response = new PosApiResponse<>();
    response.resultCode = failedResponse.resultCode;
    response.message = failedResponse.message;
    response.reason = failedResponse.data.reason;
    return response;
  }

  public static <T> PosApiResponse<T> failed(HttpStatusCode httpStatus) {
    PosApiResponse<T> response = new PosApiResponse<>();
    response.resultCode = String.valueOf(httpStatus.value());
    return response;
  }

  public boolean isOk() {
    return !isError();
  }

  public boolean isUnauthorized() {
    HttpStatus httpStatus = HttpStatus.valueOf(Integer.parseInt(resultCode));

    return httpStatus == HttpStatus.UNAUTHORIZED;
  }

  public boolean isError() {
    HttpStatus httpStatus = HttpStatus.valueOf(Integer.parseInt(resultCode));

    return httpStatus.is4xxClientError() || httpStatus.is5xxServerError();
  }

  public HttpStatus httpStatus() {
    return HttpStatus.valueOf(Integer.parseInt(resultCode));
  }

  @Getter
  public static class Reason {
    private String reason;
  }
}
