package co.wadcorp.waiting.data.domain.waiting.fixture;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.springframework.cglib.core.Local;
import org.springframework.test.util.ReflectionTestUtils;

public class WaitingFixture {

  public static WaitingEntity createWaiting(String shopId, LocalDate operationDate) {
    return createWaiting(shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.WAITING,
        null);
  }

  public static WaitingEntity createWaiting(String shopId, LocalDate operationDate, String seatOptionName) {
    return createWaiting(shopId, operationDate, WaitingStatus.WAITING,
        WaitingDetailStatus.WAITING, seatOptionName);
  }

  public static WaitingEntity createWaiting(String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus) {
    return createWaiting(shopId, operationDate, waitingStatus, waitingDetailStatus, null);
  }

  public static WaitingEntity createSittingWaiting(String shopId, LocalDate operationDate) {
    return createWaiting(shopId, operationDate, WaitingStatus.SITTING,
        WaitingDetailStatus.SITTING, null);
  }

  public static WaitingEntity createSittingWaitingWithRegDateTime(String shopId, LocalDate operationDate, ZonedDateTime zonedDateTime, ZonedDateTime regDateTime) {
    WaitingEntity waiting = createWaiting(shopId, operationDate, WaitingStatus.SITTING,
        WaitingDetailStatus.SITTING, null, zonedDateTime);

    ReflectionTestUtils.setField(waiting, "regDateTime", regDateTime);
    return waiting;
  }

  public static WaitingEntity createSittingWaiting(String shopId, LocalDate operationDate, ZonedDateTime zonedDateTime) {
    return createWaiting(shopId, operationDate, WaitingStatus.SITTING,
        WaitingDetailStatus.SITTING, null, zonedDateTime);
  }


  public static WaitingEntity createSittingWaiting(String shopId, LocalDate operationDate, String seatOptionName) {
    return createWaiting(shopId, operationDate, WaitingStatus.SITTING,
        WaitingDetailStatus.SITTING, seatOptionName);
  }

  public static WaitingEntity createWaiting(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, String seatOptionName) {
    return createWaiting(shopId, operationDate, waitingStatus, waitingDetailStatus, seatOptionName,
        ZonedDateTimeUtils.nowOfSeoul(), WaitingNumber.builder()
            .waitingNumber(1)
            .waitingOrder(1)
            .build());
  }

  public static WaitingEntity createWaiting(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, String seatOptionName, ZonedDateTime waitingCompleteDateTime) {
    return createWaiting(shopId, operationDate, waitingStatus, waitingDetailStatus, seatOptionName,
        waitingCompleteDateTime, WaitingNumber.builder()
            .waitingNumber(1)
            .waitingOrder(1)
            .build());
  }

  public static WaitingEntity createWaiting(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, String seatOptionName, WaitingNumber waitingNumber) {
    return createWaiting(shopId, operationDate, waitingStatus, waitingDetailStatus, seatOptionName,
        ZonedDateTimeUtils.nowOfSeoul(), waitingNumber);
  }


  public static WaitingEntity createWaiting(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, String seatOptionName,
      ZonedDateTime waitingCompleteDateTime, WaitingNumber waitingNumber) {
    return WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .waitingNumbers(waitingNumber)
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName(seatOptionName)
        .customerSeq(1L)
        .waitingCompleteDateTime(waitingCompleteDateTime)
        .build();
  }
}
