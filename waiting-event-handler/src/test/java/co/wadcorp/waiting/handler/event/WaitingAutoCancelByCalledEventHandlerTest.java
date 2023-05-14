package co.wadcorp.waiting.handler.event;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetRepository;
import co.wadcorp.waiting.data.event.CalledEvent;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class WaitingAutoCancelByCalledEventHandlerTest extends IntegrationTest {

  @Autowired
  private WaitingAutoCancelByCalledEventHandler eventHandler;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private WaitingHistoryRepository waitingHistoryRepository;

  @Autowired
  private AutoCancelTargetRepository autoCancelTargetRepository;

  @AfterEach
  void tearDown() {
    waitingRepository.deleteAllInBatch();
    waitingHistoryRepository.deleteAllInBatch();
    autoCancelTargetRepository.deleteAllInBatch();
  }

  @DisplayName("호출 횟수 1회인 웨이팅은 자동 취소 타겟으로 저장한다.")
  @Test
  void saveAutoCancelTarget() throws InterruptedException {
    // given
    String shopId = "shopId";

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    PersonOptionsData personOptionsData = PersonOptionsData.builder()
        .personOptions(List.of(PersonOption.builder()
            .name("유아")
            .count(1)
            .build()
        ))
        .build();

    WaitingEntity waiting = createWaiting(
        shopId, operationDate, WaitingStatus.WAITING, WaitingDetailStatus.CALL, 1,
        personOptionsData
    );
    WaitingHistoryEntity waitingHistory1 = createWaitingHistory(waiting);
    WaitingHistoryEntity waitingHistory2 = createWaitingHistory(waiting);

    CalledEvent calledEvent = new CalledEvent(shopId, waitingHistory2.getWaitingSeq(),
        ZonedDateTimeUtils.nowOfSeoul(), "deviceId");

    // when
    eventHandler.saveAutoCancelTarget(calledEvent);
    Thread.sleep(1000); // TODO: 2023/02/22 @Async test 하는 법 확인 필요함... void라 애매

    // then
    List<AutoCancelTargetEntity> results = autoCancelTargetRepository.findAll();
    assertThat(results).hasSize(1);
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, int waitingOrder, PersonOptionsData personOptionsData
  ) {
    WaitingEntity waitingEntity = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName("홀")
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(waitingOrder)
                .build()
        )
        .personOptionsData(personOptionsData)
        .build();
    return waitingRepository.save(waitingEntity);
  }

  private WaitingHistoryEntity createWaitingHistory(WaitingEntity waitingEntity) {
    WaitingHistoryEntity waitingHistory = new WaitingHistoryEntity(waitingEntity);
    return waitingHistoryRepository.save(waitingHistory);
  }

}