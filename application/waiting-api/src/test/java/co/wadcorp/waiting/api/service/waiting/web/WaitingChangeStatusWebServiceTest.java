package co.wadcorp.waiting.api.service.waiting.web;

import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingChangeStatusWebServiceTest extends IntegrationTest {

  @Autowired
  private WaitingChangeStatusWebService waitingChangeStatusWebService;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private DisablePutOffRepository disablePutOffRepository;

  @DisplayName("현재 마지막 순서인 경우 미루기가 불가능하다.")
  @Test
  void couldNotPutOff() {
    // given
    String shopId = "shopId-1";
    LocalDate operationDate = LocalDate.of(2023, 3, 17);
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("1", "11");

    int waitingOrder = 3;

    WaitingEntity waiting = createWaiting(shopId, operationDate, WAITING,
        WaitingDetailStatus.WAITING,
        waitingOrder, "홀");

    given(waitingNumberService.getMaxWaitingOrder(shopId, operationDate)).willReturn(3);
    given(waitingNumberService.incrementGetWaitingOrder(shopId, operationDate)).willReturn(4L);

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> waitingChangeStatusWebService.putOff(waiting.getWaitingId(), operationDate)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.COULD_NOT_PUT_OFF.getMessage());
  }

  @DisplayName("미루기 off 매장이면 미루기가 불가능하다.")
  @Test
  void disablePutOff() {
    // given
    String shopId = "shopId-1";
    LocalDate operationDate = LocalDate.of(2023, 3, 17);
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put("1", "11");

    createDisablePutOff(shopId);

    WaitingEntity waiting = createWaiting(shopId, operationDate, WAITING,
        WaitingDetailStatus.WAITING, 1, "홀");

    given(waitingNumberService.getMaxWaitingOrder(shopId, operationDate)).willReturn(3);

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> waitingChangeStatusWebService.putOff(waiting.getWaitingId(), operationDate)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.DISABLE_PUT_OFF.getMessage());
  }

  private WaitingEntity createWaiting(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, int waitingOrder,
      String seatOptionName
  ) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName(seatOptionName)
        .customerSeq(1L)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(waitingOrder)
                .build()
        )
        .personOptionsData(PersonOptionsData.builder().build())
        .build();
    return waitingRepository.save(waiting);
  }

  private DisablePutOffEntity createDisablePutOff(String shopId) {
    DisablePutOffEntity disablePutOff = DisablePutOffEntity.builder()
        .shopId(shopId)
        .build();
    return disablePutOffRepository.save(disablePutOff);
  }

}