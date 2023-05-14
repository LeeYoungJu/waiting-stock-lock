package co.wadcorp.waiting.data.domain.waiting.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistories;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WaitingValidatorTest {


  @DisplayName("미루기 정상")
  @Test
  void validatePutOff() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING,
        "seatOptionName", WaitingNumber.builder()
            .waitingOrder(1)
            .waitingNumber(101)
            .build()
    );

    WaitingHistoryEntity waitingHistory = new WaitingHistoryEntity(waiting);
    WaitingHistories waitingHistories = new WaitingHistories(
        List.of(waitingHistory)
    );

    // when // then
    Assertions.assertDoesNotThrow(() ->
        WaitingValidator.validatePutOff(waiting,  10,  operationDate, waitingHistories)
    );
  }


  @DisplayName("미루기를 할 때 웨이팅 중이 아니라면 예외가 발생한다.")
  @Test
  void validatePutOffNotWaitingStatus() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.SITTING, WaitingDetailStatus.SITTING,
        "seatOptionName", WaitingNumber.builder()
            .waitingOrder(1)
            .waitingNumber(101)
            .build()
    );

    WaitingHistoryEntity waitingHistory = new WaitingHistoryEntity(waiting);
    WaitingHistories waitingHistories = new WaitingHistories(
        List.of(waitingHistory)
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingValidator.validatePutOff(waiting,  1,  operationDate, waitingHistories)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo("웨이팅 중이 아니라 미루기를 할 수 없습니다. 다시 확인해주세요.");
  }



  @DisplayName("미루기를 할 때 다른 날짜의 웨이팅이라면 예외가 발생한다.")
  @Test
  void validatePutOffExpiredWaiting() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING,
        "seatOptionName", WaitingNumber.builder()
            .waitingOrder(1)
            .waitingNumber(101)
            .build()
    );

    WaitingHistoryEntity waitingHistory = new WaitingHistoryEntity(waiting);
    WaitingHistories waitingHistories = new WaitingHistories(
        List.of(waitingHistory)
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingValidator.validatePutOff(waiting,  1,  operationDate.plusDays(1), waitingHistories)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.EXPIRED_WAITING.getMessage());
  }



  @DisplayName("미루기를 할 때 이미 마지막 순서라면 예외가 발생한다.")
  @Test
  void validatePutOffAlreadyLastOrder() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING,
        "seatOptionName", WaitingNumber.builder()
            .waitingOrder(200)
            .waitingNumber(101)
            .build()
    );

    WaitingHistoryEntity waitingHistory = new WaitingHistoryEntity(waiting);
    WaitingHistories waitingHistories = new WaitingHistories(
        List.of(waitingHistory)
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingValidator.validatePutOff(waiting,  200,  operationDate, waitingHistories)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo("현재 마지막 순서이기 때문에 미루기가 불가해요");
  }

  @DisplayName("미루기를 할 때 호출한 내역이 있다면 예외가 발생한다.")
  @Test
  void validatePutOffByAfterCall() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.CALL,
        "seatOptionName", WaitingNumber.builder()
                .waitingOrder(1)
                .waitingNumber(101)
            .build()
    );

    WaitingHistoryEntity waitingHistory = new WaitingHistoryEntity(waiting);
    WaitingHistories waitingHistories = new WaitingHistories(
        List.of(waitingHistory)
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingValidator.validatePutOff(waiting,  4,  operationDate, waitingHistories)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.CANNOT_NOT_PUT_OFF_AFTER_CALL.getMessage());
  }


  @DisplayName("미루기를 할 때 미루기 횟수를 모두 사용했다면 예외가 발생한다.")
  @Test
  void validatePutOffByAllUsedPutOffCount() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";
    String seatOptionName = "seatOptionName";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING,
        seatOptionName, WaitingNumber.builder()
            .waitingOrder(1)
            .waitingNumber(101)
            .build()
    );

    WaitingHistoryEntity waitingHistory = new WaitingHistoryEntity(waiting);
    WaitingHistoryEntity putOffWaitingHistory = WaitingHistoryEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .seatOptionName(seatOptionName)
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.PUT_OFF)
        .build();
    WaitingHistories waitingHistories = new WaitingHistories(
        List.of(waitingHistory, putOffWaitingHistory, putOffWaitingHistory, putOffWaitingHistory)
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingValidator.validatePutOff(waiting,  4,  operationDate, waitingHistories)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo("미루기 횟수를 이미 모두 사용했습니다.");
  }
}