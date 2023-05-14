package co.wadcorp.waiting.handler.event;

import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.CREATED;
import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.IGNORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetRepository;
import co.wadcorp.waiting.data.event.SeatedEvent;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class WaitingAutoCancelTargetIgnoreEventHandlerTest extends IntegrationTest {

  @Autowired
  private WaitingAutoCancelTargetIgnoreEventHandler eventHandler;

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

  @DisplayName("착석 이벤트 시 자동 취소 대상자인지 체크 후 맞다면 무시하도록 설정")
  @Test
  void ignoreTargetBySeated() throws InterruptedException {
    // given
    ZonedDateTime now = ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 2, 23, 18, 0));
    String shopId = "shopId";

    WaitingEntity waiting = createWaiting(
        shopId, now.toLocalDate(), WaitingStatus.WAITING, WaitingDetailStatus.SITTING, 1,
        PersonOptionsData.builder()
            .personOptions(List.of(PersonOption.builder()
                .name("유아")
                .count(1)
                .build()
            ))
            .build()
    );
    WaitingHistoryEntity waitingHistory = createWaitingHistory(waiting);

    AutoCancelTargetEntity target = createTarget(shopId, waitingHistory.getWaitingId(), CREATED,
        now.minusSeconds(1));
    autoCancelTargetRepository.save(target);

    // when
    eventHandler.ignoreTargetBySeated(new SeatedEvent(shopId, waitingHistory.getWaitingSeq(), now.toLocalDate(), "deviceId"));
    Thread.sleep(1000); // TODO: 2023/02/23 @Async test 하는 법 확인 필요함... void라 애매

    // then
    List<AutoCancelTargetEntity> results = autoCancelTargetRepository.findAll();
    assertThat(results).hasSize(1)
        .extracting("waitingId", "processingStatus")
        .contains(
            tuple(waitingHistory.getWaitingId(), IGNORE)
        );
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

  private static AutoCancelTargetEntity createTarget(String shopId, String waitingId,
      AutoCancelProcessingStatus processingStatus, ZonedDateTime expectedCancelDateTime) {
    return AutoCancelTargetEntity.builder()
        .shopId(shopId)
        .waitingId(waitingId)
        .processingStatus(processingStatus)
        .expectedCancelDateTime(expectedCancelDateTime)
        .build();
  }

}