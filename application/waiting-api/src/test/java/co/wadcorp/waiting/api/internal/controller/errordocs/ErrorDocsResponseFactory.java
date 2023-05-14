package co.wadcorp.waiting.api.internal.controller.errordocs;

import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.data.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorDocsResponseFactory {

  static <T> ResponseEntity<ApiResponse<T>> make(ErrorCode errorCode, T data) {
    ApiResponse response = new ApiResponse();
    response.setResultCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
    response.setMessage(errorCode.getMessage());
    response.setDisplayMessage(errorCode.getMessage());
    response.setData(data);

    return ResponseEntity.badRequest().body(response);
  }
}
