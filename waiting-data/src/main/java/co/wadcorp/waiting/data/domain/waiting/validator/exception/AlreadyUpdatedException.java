package co.wadcorp.waiting.data.domain.waiting.validator.exception;

import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyUpdatedException extends AppException {

  public AlreadyUpdatedException(HttpStatus httpStatus, String displayMessage) {
    super(httpStatus, displayMessage);
  }

  public AlreadyUpdatedException(HttpStatus httpStatus, ErrorCode errorCode) {
    super(httpStatus, errorCode);
  }

}
