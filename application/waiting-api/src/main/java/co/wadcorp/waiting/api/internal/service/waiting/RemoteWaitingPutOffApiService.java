package co.wadcorp.waiting.api.internal.service.waiting;

import co.wadcorp.waiting.api.internal.service.waiting.dto.response.AdditionalOptionDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.PersonOptionDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.TableDto;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingShopIdValidator;
import co.wadcorp.waiting.data.event.PutOffEvent;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.query.settings.DisablePutOffQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.data.service.waiting.WaitingChangeStatusService;
import co.wadcorp.waiting.data.service.waiting.WaitingNumberService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class RemoteWaitingPutOffApiService {

  private static final String CATCH_TABLE_APP = "CATCH_TABLE_APP";
  private final WaitingChangeStatusService waitingChangeStatusByCustomerService;
  private final WaitingService waitingService;
  private final HomeSettingsService homeSettingsService;
  private final ShopService shopService;
  private final CustomerService customerService;
  private final WaitingNumberService waitingNumberService;
  private final WaitingCountQueryRepository waitingCountQueryRepository;
  private final DisablePutOffQueryRepository disablePutOffQueryRepository;

  private final ApplicationEventPublisher eventPublisher;

  public RemoteWaitingResponse putOff(ChannelShopIdMapping channelShopIdMapping, String waitingId,
      LocalDate operationDate, ZonedDateTime nowDateTime
  ) {
    channelShopIdMapping.checkOnlyOneShopId();
    String waitingShopId = channelShopIdMapping.getFirstWaitingShopId();

    WaitingEntity waiting = waitingService.findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(waitingShopId, waiting);

    Integer currentWaitingOrder = waitingNumberService.getMaxWaitingOrder(waitingShopId, operationDate);
    if (waiting.isSameWaitingOrder(currentWaitingOrder)) { // 이미 마지막 순번이면 미루기 불가
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.COULD_NOT_PUT_OFF);
    }

    if (disablePutOffQueryRepository.isShopDisabledPutOff(waitingShopId)) { // 미루기 off 매장
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.DISABLE_PUT_OFF);
    }

    Long maxWaitingOrder = waitingNumberService.incrementGetWaitingOrder(waitingShopId,
        operationDate);

    WaitingHistoryEntity waitingHistory = waitingChangeStatusByCustomerService.putOff(
        waitingId,
        operationDate,
        maxWaitingOrder
    );
    CustomerEntity customer = customerService.findById(waitingHistory.getCustomerSeq());

    eventPublisher.publishEvent(
        new PutOffEvent(
            waitingHistory.getShopId(),
            waitingHistory.getSeq(),
            operationDate,
            waiting.getWaitingOrder(),
            waitingHistory.getWaitingOrder(),
            CATCH_TABLE_APP
        )
    );

    ShopEntity shopEntity = shopService.findByShopId(waitingHistory.getShopId());

    int waitingTeamCount = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
        waitingHistory.getShopId(),
        operationDate, waitingHistory.getWaitingOrder(), waitingHistory.getSeatOptionName());

    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(waitingShopId)
        .getHomeSettingsData();
    SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionName(
        waitingHistory.getSeatOptionName()
    );

    return RemoteWaitingResponse.builder()
        .id(waitingHistory.getWaitingId())
        .shopId(Long.valueOf(channelShopIdMapping.getChannelShopId(waitingShopId)))
        .shopName(shopEntity.getShopName())
        .registerChannel(waitingHistory.getRegisterChannel())
        .operationDate(waitingHistory.getOperationDate())
        .customerPhoneNumber(customer.getEncCustomerPhone().getLocal())
        .waitingNumber(waitingHistory.getWaitingNumber())
        .waitingOrder(waitingTeamCount)
        .waitingRegisteredOrder(waitingHistory.getWaitingOrder())
        .waitingStatus(waitingHistory.getWaitingStatus())
        .waitingDetailStatus(waitingHistory.getWaitingDetailStatus())
        .totalPersonCount(waitingHistory.getTotalPersonCount())
        .expectedSittingDateTime(
            getExpectedSittingDateTime(nowDateTime, seatOptions, waitingTeamCount))
        .waitingCompleteDateTime(waitingHistory.getWaitingCompleteDateTime())
        .personOptions(
            convertPersonOptions(waitingHistory.getPersonOptionsData())
        )
        .table(TableDto.builder()
            .id(seatOptions.getId())
            .name(seatOptions.getName())
            .isTakeOut(seatOptions.getIsTakeOut())
            .build()
        )
        .build();
  }

  private ZonedDateTime getExpectedSittingDateTime(ZonedDateTime nowDateTime,
      SeatOptions seatOptions, Integer waitingTeamCount) {
    if (seatOptions.isNotUseExpectedWaitingPeriod()) {
      return null;
    }

    Integer expectedWaitingPeriod = seatOptions.calculateExpectedWaitingPeriod(waitingTeamCount);
    return nowDateTime.plusMinutes(expectedWaitingPeriod);
  }

  private List<PersonOptionDto> convertPersonOptions(PersonOptionsData personOptionsData) {
    List<PersonOption> personOptions = personOptionsData.getPersonOptions();

    return personOptions.stream()
        .map(po ->
            PersonOptionDto.builder()
                .name(po.getName())
                .count(po.getCount())
                .additionalOptions(
                    po.getAdditionalOptions().stream()
                        .map(ao ->
                            AdditionalOptionDto.builder()
                                .name(ao.getName())
                                .count(ao.getCount())
                                .build()
                        )
                        .toList()
                )
                .build()
        )
        .toList();
  }
}
