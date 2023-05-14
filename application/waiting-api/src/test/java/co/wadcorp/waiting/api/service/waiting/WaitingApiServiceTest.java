package co.wadcorp.waiting.api.service.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.model.waiting.response.WaitingResponse;
import co.wadcorp.waiting.data.domain.message.MessageSendHistoryEntity;
import co.wadcorp.waiting.data.domain.message.MessageSendHistoryRepository;
import co.wadcorp.waiting.data.domain.message.SendChannel;
import co.wadcorp.waiting.data.domain.message.SendStatus;
import co.wadcorp.waiting.data.domain.message.SendType;
import co.wadcorp.waiting.data.domain.order.OrderRepository;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.WaitingQueryRepository;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingApiServiceTest extends IntegrationTest {

  @Autowired
  private WaitingHistoryRepository waitingHistoryRepository;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private MessageSendHistoryRepository messageSendHistoryRepository;

  @Autowired
  private WaitingApiService waitingApiService;

  @DisplayName("웨이팅을 조회할 수 있다.")
  @Test
  void getWaitingBy() {
    // given
    String shopId = UUIDUtil.shortUUID();
    String waitingId = UUIDUtil.shortUUID();
    LocalDate operationDate = LocalDate.of(2023, 4, 17);

    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(waitingId)
        .operationDate(operationDate)
        .registerChannel(RegisterChannel.WAITING_APP)
        .waitingNumbers(WaitingNumber.builder()
            .waitingNumber(101)
            .waitingOrder(1)
            .build())
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.CALL)
        .seatOptionName("좌석")
        .customerSeq(1L)
        .totalPersonCount(2)
        .personOptionsData(PersonOptionsData.builder()
            .personOptions(List.of())
            .build())
        .build();
    waitingRepository.save(waiting);

    waitingHistoryRepository.save(new WaitingHistoryEntity(waiting));
    messageSendHistoryRepository.save(MessageSendHistoryEntity.builder()
        .waitingHistorySeq(1L)
        .waitingId(waitingId)
        .requestId("123-1")
        .encCustomerPhone(PhoneNumberUtils.ofKr("010-0000-0001"))
        .sendChannel(SendChannel.ALIMTALK)
        .templateName("")
        .sendType(SendType.WAITING_READY_TO_ENTER)
        .templateCode("")
        .content("")
        .buttons("")
        .status(SendStatus.SUCCESS)
        .failCode("")
        .failReason("")
        .sendDateTime(ZonedDateTimeUtils.nowOfSeoul())
        .build());

    // when
    WaitingResponse waitingBy = waitingApiService.getWaitingBy(waitingId);

    // then
    assertThat(waitingBy.getWaiting())
        .extracting("waitingId", "shopId", "operationDate", "registerChannel",
            "waitingStatus", "waitingDetailStatus")
        .containsExactly(
            waiting.getWaitingId(), waiting.getShopId(), waiting.getOperationDate().toString(),
            waiting.getRegisterChannel(),
            waiting.getWaitingStatus(), waiting.getWaitingDetailStatus()
        );
  }
}