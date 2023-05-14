
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
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.data.service.waiting.WaitingChangeStatusService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class RemoteWaitingCancelApiService {
  private static final String CATCH_TABLE_APP = "CATCH_TABLE_APP";

  private final ShopCustomerService shopCustomerService;
  private final ShopService shopService;
  private final CustomerService customerService;
  private final HomeSettingsService homeSettingsService;
  private final WaitingChangeStatusService waitingChangeStatusService;
  private final WaitingService waitingService;

  private final WaitingCountQueryRepository waitingCountQueryRepository;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public RemoteWaitingResponse cancel(
      ChannelShopIdMapping channelShopIdMapping,
      String waitingId,
      LocalDate operationDate,
      ZonedDateTime nowDateTime
  ) {

    channelShopIdMapping.checkOnlyOneShopId();
    String waitingShopId = channelShopIdMapping.getFirstWaitingShopId();

    WaitingEntity waiting = waitingService.findByWaitingId(waitingId);
    WaitingShopIdValidator.validateWaitingSameShopId(waitingShopId, waiting);


    WaitingHistoryEntity waitingHistory = waitingChangeStatusService.cancelByCustomer(waitingId);
    CustomerEntity customer = customerService.findById(waitingHistory.getCustomerSeq());
    shopCustomerService.cancel(waitingHistory.getShopId(), waitingHistory.getCustomerSeq());

    eventPublisher.publishEvent(
        new CanceledByCustomerEvent(waitingHistory.getShopId(), waitingHistory.getSeq(),
            operationDate, CATCH_TABLE_APP)
    );

    ShopEntity shopEntity = shopService.findByShopId(waitingHistory.getShopId());

    int waitingTeamCount = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
        waitingHistory.getShopId(),
        operationDate, waitingHistory.getWaitingOrder(), waitingHistory.getSeatOptionName());

    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(waitingShopId)
        .getHomeSettingsData();
    SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionName(
        waitingHistory.getSeatOptionName());

    return RemoteWaitingResponse.builder()
        .id(waitingHistory.getWaitingId())
        .shopId(Long.valueOf(channelShopIdMapping.getChannelShopId(waitingShopId)))
        .shopName(shopEntity.getShopName())
        .registerChannel(waitingHistory.getRegisterChannel())
        .operationDate(waitingHistory.getOperationDate())
        .customerPhoneNumber(customer.getLocalPhoneNumber())
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
        .regDateTime(waitingHistory.getRegDateTime())
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
