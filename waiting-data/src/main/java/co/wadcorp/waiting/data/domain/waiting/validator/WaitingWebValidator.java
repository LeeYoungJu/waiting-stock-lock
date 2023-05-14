package co.wadcorp.waiting.data.domain.waiting.validator;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingWebValidator {

  public static void validate(WaitingEntity waiting, LocalDate operationDate) {
    validateOperationDate(waiting, operationDate);
    validateIsSitting(waiting);
    validateIsCanceled(waiting);
  }

  private static void validateOperationDate(WaitingEntity waiting, LocalDate operationDate) {
    if (waiting.isSameOperationDate(operationDate)) {
      return;
    }
    throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.EXPIRED_WAITING);
  }

  private static void validateIsSitting(WaitingEntity waiting) {
    if (waiting.isSitting()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.SITTING_WAITING);
    }
  }

  private static void validateIsCanceled(WaitingEntity waiting) {
    if (!waiting.isCanceled()) {
      return;
    }
    switch (waiting.getWaitingDetailStatus()) {
      case CANCEL_BY_CUSTOMER ->
          throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.CANCELED_WAITING_BY_CUSTOMER);
      case CANCEL_BY_SHOP ->
          throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.CANCELED_WAITING_BY_SHOP);
      case CANCEL_BY_NO_SHOW ->
          throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.CANCELED_WAITING_BY_NO_SHOW);
    }
  }
}
