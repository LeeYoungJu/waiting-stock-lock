package co.wadcorp.waiting.data.domain.waiting.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.fixture.WaitingFixture;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WaitingWebValidatorTest {

  @DisplayName("주어진 영업일이 아니면 예외를 발생시킨다.")
  @Test
  void validateOperationDate() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(shopId, operationDate.minusDays(1));

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingWebValidator.validate(waiting, operationDate)
    );

    assertThat(appException.getDisplayMessage()).isEqualTo(ErrorCode.EXPIRED_WAITING.getMessage());
  }

  @DisplayName("착석 상태의 웨이팅이면 예외를 발생시킨다.")
  @Test
  void validateIsSitting() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.SITTING, WaitingDetailStatus.SITTING, "seatOptionName"
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingWebValidator.validate(waiting, operationDate)
    );

    assertThat(appException.getDisplayMessage()).isEqualTo(ErrorCode.SITTING_WAITING.getMessage());
  }

  @DisplayName("고객 요청으로 취소된 경우 예외가 발생한다.")
  @Test
  void validateIsCanceledByCustomer() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.CANCEL, WaitingDetailStatus.CANCEL_BY_CUSTOMER,
        "seatOptionName"
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingWebValidator.validate(waiting, operationDate)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.CANCELED_WAITING_BY_CUSTOMER.getMessage());
  }

  @DisplayName("업주 요청으로 취소된 경우 예외가 발생한다.")
  @Test
  void validateIsCanceledByShop() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.CANCEL, WaitingDetailStatus.CANCEL_BY_SHOP,
        "seatOptionName"
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingWebValidator.validate(waiting, operationDate)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.CANCELED_WAITING_BY_SHOP.getMessage());
  }

  @DisplayName("노쇼로 취소된 경우 예외가 발생한다.")
  @Test
  void validateIsCanceledByNoShow() {
    // given
    LocalDate operationDate = LocalDate.of(2023, 2, 3);
    String shopId = "123";

    WaitingEntity waiting = WaitingFixture.createWaiting(
        shopId, operationDate, WaitingStatus.CANCEL, WaitingDetailStatus.CANCEL_BY_NO_SHOW,
        "seatOptionName"
    );

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> WaitingWebValidator.validate(waiting, operationDate)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.CANCELED_WAITING_BY_NO_SHOW.getMessage());
  }

}