package co.wadcorp.waiting.data.domain.waiting.validator;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.validator.exception.AlreadyUpdatedException;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingSittingValidator {

  public static void validate(WaitingEntity waiting, LocalDate operationDate) {
    validateWaitingStatus(waiting);
    validateTodayOperationDate(waiting, operationDate);
  }

  private static void validateWaitingStatus(WaitingEntity waiting) {
    if (waiting.isSitting()) {
      throw new AlreadyUpdatedException(HttpStatus.CREATED, ErrorCode.ALREADY_SITTING_WAITING);
    }

    if (!waiting.isWaitingStatus()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_WAITING_STATUS_BY_SITTING);
    }
  }

  private static void validateTodayOperationDate(WaitingEntity waiting, LocalDate operationDate) {
    if (waiting.isSameOperationDate(operationDate)) {
      return;
    }
    throw new AppException(HttpStatus.BAD_REQUEST, "같은 영업일이 아닌 웨이팅은 착석으로 변경할 수 없습니다. 다시 확인해주세요.");
  }
}
