package co.wadcorp.waiting.api.service.waiting.web;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WaitingWebResponse;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WebCustomerWaitingListResponse;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WebCustomerWaitingListResponse.WebCustomerWaiting;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.query.customer.CustomerQueryRepository;
import co.wadcorp.waiting.data.query.order.WaitingOrderQueryRepository;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import co.wadcorp.waiting.data.query.shop.ShopQueryRepository;
import co.wadcorp.waiting.data.query.shop.dto.ShopDto;
import co.wadcorp.waiting.data.query.settings.DisablePutOffQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingHistoryQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingPageListQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto.WaitingDto;
import co.wadcorp.waiting.data.query.waiting.dto.WebWaitingDto;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.settings.PrecautionSettingsService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WaitingWebService {

  private final HomeSettingsService homeSettingsService;
  private final PrecautionSettingsService precautionSettingsService;
  private final WaitingCountQueryRepository waitingCountQueryRepository;
  private final WaitingPageListQueryRepository waitingPageListQueryRepository;
  private final WaitingHistoryQueryRepository waitingHistoryQueryRepository;
  private final ShopQueryRepository shopQueryRepository;
  private final CustomerQueryRepository customerQueryRepository;
  private final HomeSettingsQueryRepository homeSettingsQueryRepository;
  private final WaitingOrderQueryRepository waitingOrderQueryRepository;
  private final DisablePutOffQueryRepository disablePutOffQueryRepository;

  public WaitingWebResponse getWaitingInfo(String waitingId, LocalDate operationDateFromNow) {
    WaitingHistoriesDto waitingHistoriesDto = waitingHistoryQueryRepository.getWaitingHistories(
        waitingId);

    if (Objects.isNull(waitingHistoriesDto.getWaiting())) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_WAITING);
    }

    WaitingDto waitingDto = waitingHistoriesDto.getWaiting();
    ShopDto shopDto = shopQueryRepository.getByShopId(waitingDto.getShopId());

    // TODO Query로 변경 필요함
    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(waitingDto.getShopId())
        .getHomeSettingsData();
    PrecautionSettingsEntity precautionSettings = precautionSettingsService.getPrecautionSettings(
        shopDto.getShopId());

    int waitingTeamCount = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
        waitingDto.getShopId(),
        operationDateFromNow, waitingDto.getWaitingOrder(), waitingDto.getSeatOptionName());

    SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionName(
        waitingDto.getSeatOptionName());
    Integer expectedWaitingPeriod = getExpectedWaitingPeriod(waitingTeamCount, seatOptions);

    WaitingOrderDto waitingOrderDto = findOrderByWaitingId(waitingId);

    boolean disablePutOff = disablePutOffQueryRepository.isShopDisabledPutOff(
        waitingDto.getShopId());

    return WaitingWebResponse.toDto(waitingDto, shopDto, waitingTeamCount, expectedWaitingPeriod,
        waitingHistoriesDto.getCanPutOffCount(), precautionSettings.getPrecautions(),
        waitingOrderDto, disablePutOff);
  }

  public WebCustomerWaitingListResponse getAllCustomerWaitingByWaitingId(String waitingId,
      LocalDate operationDateFromNow) {
    CustomerEntity customer = customerQueryRepository.getCustomerBy(waitingId)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CUSTOMER));

    List<WebWaitingDto> waitings = waitingPageListQueryRepository.getAllWaitingByCustomer(
        customer.getSeq(), operationDateFromNow);

    Map<String, HomeSettingsEntity> homeSettingsMap = createHomeSettingsMap(waitings);

    return new WebCustomerWaitingListResponse(
        customer.getEncCustomerPhone(),
        waitings.stream()
            .map(waiting -> createWaitingResponse(operationDateFromNow, homeSettingsMap, waiting))
            .toList()
    );
  }

  private Integer getExpectedWaitingPeriod(int waitingTeamCount, SeatOptions seatOptions) {
    if (seatOptions.isNotUseExpectedWaitingPeriod()) {
      return null;
    }

    return seatOptions.calculateExpectedWaitingPeriod(waitingTeamCount);
  }

  private WebCustomerWaiting createWaitingResponse(LocalDate operationDateFromNow,
      Map<String, HomeSettingsEntity> homeSettingsMap, WebWaitingDto waiting) {
    int waitingOrder = getWaitingOrder(operationDateFromNow, waiting);

    HomeSettingsEntity homeSettings = homeSettingsMap.getOrDefault(
        waiting.getShopId(), createDefaultHomeSettings(waiting.getShopId())
    );
    HomeSettingsData homeSettingsData = homeSettings.getHomeSettingsData();
    Integer expectedWaitingPeriodSetting = getExpectedPeriodBySeatOption(waiting,
        homeSettingsData);

    return WebCustomerWaiting.toDto(waiting, homeSettingsData, waitingOrder,
        expectedWaitingPeriodSetting);
  }

  private Map<String, HomeSettingsEntity> createHomeSettingsMap(List<WebWaitingDto> waitings) {
    List<String> shopIds = extractShopIdsFrom(waitings);
    List<HomeSettingsEntity> homeSettings = homeSettingsQueryRepository.findByShopIds(shopIds);

    return homeSettings.stream()
        .collect(
            Collectors.toMap(HomeSettingsEntity::getShopId, item -> item, (item1, item2) -> item1));
  }

  private List<String> extractShopIdsFrom(List<WebWaitingDto> waitings) {
    return convert(waitings, WebWaitingDto::getShopId);
  }

  private int getWaitingOrder(LocalDate operationDateFromNow, WebWaitingDto waiting) {
    return waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
        waiting.getShopId(), operationDateFromNow, waiting.getWaitingOrder(),
        waiting.getSeatOptionName()
    );
  }

  private Integer getExpectedPeriodBySeatOption(WebWaitingDto waiting,
      HomeSettingsData homeSettingsData) {
    return homeSettingsData
        .getExpectedPeriodBySeatOption(waiting.getSeatOptionName());
  }

  private HomeSettingsEntity createDefaultHomeSettings(String shopId) {
    return new HomeSettingsEntity(shopId, DefaultHomeSettingDataFactory.create());
  }


  private WaitingOrderDto findOrderByWaitingId(String waitingId) {
    return waitingOrderQueryRepository.findByWaitingIds(
            List.of(waitingId))
        .stream().filter(item -> item.getWaitingId().equals(waitingId))
        .findFirst()
        .orElse(WaitingOrderDto.EMPTY_ORDER);

  }

}
