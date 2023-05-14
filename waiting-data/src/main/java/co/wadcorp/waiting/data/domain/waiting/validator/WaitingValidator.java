package co.wadcorp.waiting.data.domain.waiting.validator;

import static co.wadcorp.waiting.data.exception.ErrorCode.CANNOT_UNDO_CAUSE_MORE_THAN_THREE_WAITINGS;
import static co.wadcorp.waiting.data.exception.ErrorCode.SAME_SHOP_WAITING_REGISTERED;

import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistories;
import co.wadcorp.waiting.data.domain.waiting.validator.exception.AlreadyUpdatedException;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingValidator {

  private static final int MAXIMUM_WAITING_SIZE = 3;

  public static void validateCalling(WaitingEntity waiting,
      LocalDate operationDate, WaitingHistories histories) {

    validateWaitingStatus(waiting);
    validateTodayOperationDate(waiting, operationDate);
    validateCallCount(histories);
  }

  public static void validatePutOff(WaitingEntity waiting, int latestOrder,
      LocalDate operationDate,
      WaitingHistories waitingHistories) {

    validateWaitingStatusByPutOff(waiting);
    validateTodayOperationDate(waiting, operationDate);
    validateAlreadyLastOrder(waiting, latestOrder);
    validateCanPuttOff(waitingHistories);
  }


  public static void validateDelayed(WaitingEntity waiting, LocalDate operationDate) {

    validateTodayOperationDate(waiting, operationDate);
    if (!waiting.isWaitingStatus()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_WAITING_STATUS_BY_DELAY);
    }
  }

  public static void validateUndo(WaitingEntity waiting, List<WaitingEntity> waitingEntities) {
    if (waiting.isWaitingStatus()) {
      throw new AlreadyUpdatedException(HttpStatus.CREATED, "이미 복귀된 웨이팅 입니다. 다시 확인해주세요.");
    }

    if (!waiting.canUndo(ZonedDateTimeUtils.nowOfSeoul())) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.TIME_OVER_UNDO_AVAILABLE_TIME);
    }

    validateUndoWaiting(waiting, waitingEntities);
  }

  public static void validateUndoByCustomer(WaitingEntity waiting,
      List<WaitingEntity> waitingEntities) {

    validateStatus(waiting);
    validateUndoAvailable(waiting);
    validateUndoWaiting(waiting, waitingEntities);
  }

  public static void validateReadyToEnter(WaitingEntity waiting, LocalDate operationDate,
      WaitingHistories histories) {

    validateWaitingStatus(waiting);
    validateTodayOperationDate(waiting, operationDate);
    validateReadyToEnterCount(histories);
  }


  private static void validateStatus(WaitingEntity waiting) {
    if (waiting.isWaitingStatus()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_UNDO_WAITING);
    }

    if (!waiting.canUndo(ZonedDateTime.now())) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.TIME_OVER_UNDO_AVAILABLE_TIME);
    }

  }

  /**
   * 착석처리에 의한 취소가 아닌 웨이팅은 복구할 수 없다.
   */
  private static void validateUndoAvailable(WaitingEntity waiting) {
    if (waiting.getWaitingDetailStatus() != WaitingDetailStatus.CANCEL_BY_SITTING) {
      throw new AppException(HttpStatus.BAD_REQUEST, "되돌리기 할 수 없습니다.");
    }
  }


  private static void validateCallCount(WaitingHistories histories) {
    if (!histories.canCall()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "호출은 2회를 초과할 수 없습니다.");
    }
  }

  private static void validateWaitingStatus(WaitingEntity waiting) {
    if (waiting.isWaitingStatus()) {
      return;
    }
    throw new AppException(HttpStatus.BAD_REQUEST, "웨이팅 중이 아니라 호출할 수 없습니다. 다시 확인해주세요.");
  }

  private static void validateWaitingStatusByPutOff(WaitingEntity waiting) {
    if (waiting.isWaitingStatus()) {
      return;
    }
    throw new AppException(HttpStatus.BAD_REQUEST, "웨이팅 중이 아니라 미루기를 할 수 없습니다. 다시 확인해주세요.");
  }

  private static void validateTodayOperationDate(WaitingEntity waiting, LocalDate operationDate) {
    if (waiting.isSameOperationDate(operationDate)) {
      return;
    }
    throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.EXPIRED_WAITING);
  }

  /**
   * 이미 마지막 순번이면 미루기 불가
   */
  private static void validateAlreadyLastOrder(WaitingEntity waiting, int latestOrder) {
    if (waiting.getWaitingOrder() >= latestOrder) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.COULD_NOT_PUT_OFF);
    }
  }

  private static void validateCanPuttOff(WaitingHistories histories) {
    if (histories.anyMatchCall()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.CANNOT_NOT_PUT_OFF_AFTER_CALL);
    }

    if (!histories.canPutOffCount()) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.ALL_USED_PUT_OFF_COUNT);
    }
  }


  private static void validateUndoWaiting(WaitingEntity waiting,
      List<WaitingEntity> waitingEntities) {
    boolean isSameCustomerSeq = waitingEntities.stream()
        .anyMatch(item -> item.getShopId().equals(waiting.getShopId()));

    if (isSameCustomerSeq) {
      throw new AppException(HttpStatus.BAD_REQUEST, SAME_SHOP_WAITING_REGISTERED);
    }

    if (waitingEntities.size() >= MAXIMUM_WAITING_SIZE) {
      throw new AppException(HttpStatus.BAD_REQUEST, CANNOT_UNDO_CAUSE_MORE_THAN_THREE_WAITINGS);
    }
  }

  private static void validateReadyToEnterCount(WaitingHistories histories) {
    if (!histories.canReadyToEnter()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "이미 입장 준비를 했습니다.");
    }
  }

}
