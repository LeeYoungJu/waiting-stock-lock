package co.wadcorp.waiting.data.domain.waiting.validator;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.validator.exception.AlreadyUpdatedException;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingCancelValidator {

  public static void validateStatus(WaitingEntity waiting) {
    if (waiting.isCancelStatus()) {
      throw new AlreadyUpdatedException(HttpStatus.CREATED, ErrorCode.ALREADY_CANCELED_WAITING);
    }

    if (!waiting.isWaitingStatus()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_WAITING_STATUS);
    }
  }

}
