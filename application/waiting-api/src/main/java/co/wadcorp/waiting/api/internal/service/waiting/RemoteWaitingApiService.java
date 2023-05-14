package co.wadcorp.waiting.api.internal.service.waiting;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.libs.stream.StreamUtils.convertToMap;
import static co.wadcorp.libs.stream.StreamUtils.groupingBySet;
import static co.wadcorp.waiting.data.domain.customer.CustomerEntity.EMPTY_CUSTOMER_ENTITY;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingCheckBeforeRegisterServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingListServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingOfOtherShopsServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteListWaitingResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteOtherWaitingListResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingListOrderMenuDto;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.service.channel.ChannelService;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingRegisterValidator;
import co.wadcorp.waiting.data.query.order.WaitingOrderQueryRepository;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import co.wadcorp.waiting.data.query.shop.ShopQueryRepository;
import co.wadcorp.waiting.data.query.shop.dto.ShopDto;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingRegisterQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOfOtherShopQueryDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOrderCountDto;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RemoteWaitingApiService {

  private final ChannelService channelService;
  private final WaitingService waitingService;
  private final CustomerService customerService;
  private final HomeSettingsService homeSettingsService;

  private final WaitingRepository waitingRepository;
  private final ShopQueryRepository shopQueryRepository;
  private final WaitingRegisterQueryRepository waitingRegisterQueryRepository;
  private final WaitingCountQueryRepository waitingCountQueryRepository;
  private final HomeSettingsQueryRepository homeSettingsQueryRepository;
  private final WaitingOrderQueryRepository waitingOrderQueryRepository;

  public List<RemoteListWaitingResponse> findWaitings(RemoteWaitingListServiceRequest request,
      ZonedDateTime nowDateTime) {
    List<String> waitingIds = request.getWaitingIds();
    LocalDate operationDate = request.getOperationDate();

    List<WaitingEntity> waitings = waitingRepository.findAllByWaitingIdInAndOperationDate(
        waitingIds, operationDate);

    Map<String, RemoteWaitingListOrderMenuDto> orderMap = createOrderMap(waitingIds);

    Map<Long, String> customerMap = createCustomerMap(waitings);

    List<String> shopIds = extractShopIdsFrom(waitings);
    Map<String, ShopDto> shopDtoMap = createShopDtoMap(shopIds);
    Map<String, HomeSettingsData> homeSettingsDataMap = createHomeSettingsDataMap(shopIds);
    Map<String, Set<WaitingOrderCountDto>> groupingWaitingOrderMap = createWaitingOrderMap(
        operationDate, shopIds);

    return waitings.stream()
        .map(waiting -> {
              String customerPhoneNumber = customerMap.get(waiting.getCustomerSeq());

              ShopDto shopDto = shopDtoMap.getOrDefault(waiting.getShopId(), ShopDto.EMPTY);

          RemoteWaitingListOrderMenuDto orderMenuDto = orderMap.getOrDefault(waiting.getWaitingId(),
              RemoteWaitingListOrderMenuDto.EMPTY);

          if (waiting.isWaitingStatus()) {
                int waitingTeamCount = calculateWaitingTeamCount(groupingWaitingOrderMap, waiting);

                HomeSettingsData homeSettings = homeSettingsDataMap.getOrDefault(waiting.getShopId(),
                    DefaultHomeSettingDataFactory.create());
                SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionName(
                    waiting.getSeatOptionName());

                return RemoteListWaitingResponse.of(waiting, shopDto.getShopName(),
                    customerPhoneNumber, waitingTeamCount, seatOptions, nowDateTime, orderMenuDto);
              }

              return RemoteListWaitingResponse.of(waiting, shopDto.getShopName(),
                  customerPhoneNumber, orderMenuDto);
            }
        )
        .toList();
  }

  @Transactional
  public void checkWaitingBeforeRegister(
      ChannelShopIdMapping channelShopIdMapping,
      LocalDate operationDate,
      RemoteWaitingCheckBeforeRegisterServiceRequest request
  ) {
    channelShopIdMapping.checkOnlyOneShopId();

    PhoneNumber encCustomerPhone = PhoneNumberUtils.ofKr(request.getCustomerPhone());

    CustomerEntity customer = customerService.getCustomerByCustomerPhone(encCustomerPhone);
    if (customer == EMPTY_CUSTOMER_ENTITY) {
      return;
    }
    // 중복 웨이팅 검증
    List<WaitingEntity> waitingList = waitingService.getWaitingByCustomerSeqToday(
        customer.getSeq(),
        operationDate);
    WaitingRegisterValidator.validate(channelShopIdMapping.getFirstWaitingShopId(), waitingList);
  }

  public List<RemoteOtherWaitingListResponse> findAllWaitingsOfOtherShops(
      ChannelShopIdMapping channelShopIdMapping,
      LocalDate operationDate,
      RemoteWaitingOfOtherShopsServiceRequest request
  ) {
    channelShopIdMapping.checkOnlyOneShopId();

    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(request.getCustomerPhone());
    List<WaitingOfOtherShopQueryDto> otherWaitings = waitingRegisterQueryRepository.getAllWaitingOfOtherShopByCustomerPhone(
        phoneNumber,
        operationDate
    );

    Map<String, HomeSettingsEntity> homeSettingsMap = createHomeSettingsMap(otherWaitings);

    List<String> shopIds = convert(otherWaitings, WaitingOfOtherShopQueryDto::getShopId);
    Map<String, String> shopSeqMappings = createShopSeqMappings(shopIds);

    return otherWaitings.stream()
        .filter(o -> !channelShopIdMapping.getFirstWaitingShopId().equals(o.getShopId()))
        .map(waiting -> {
          int waitingOrder = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
              waiting.getShopId(),
              operationDate,
              waiting.getWaitingOrder(),
              waiting.getSeatOptionName()
          );

          HomeSettingsEntity homeSettings = homeSettingsMap.getOrDefault(
              waiting.getShopId(),
              createDefaultHomeSettings(waiting.getShopId())
          );

          HomeSettingsData homeSettingsData = homeSettings.getHomeSettingsData();
          SeatOptions seatOptionsBySeatOptionName = homeSettingsData.findSeatOptionsBySeatOptionName(
              waiting.getSeatOptionName());

          Integer expectedWaitingPeriodSetting = getExpectedPeriodBySeatOption(
              waiting.getSeatOptionName(),
              homeSettingsData
          );

          String shopSeq = shopSeqMappings.get(waiting.getShopId());
          return RemoteOtherWaitingListResponse.builder()
              .waitingId(waiting.getWaitingId())
              .shopId(shopSeq != null
                  ? Long.valueOf(shopSeq)
                  : null
              )
              .shopName(waiting.getShopName())
              .waitingOrder(waitingOrder)
              .expectedWaitingPeriod(expectedWaitingPeriodSetting)
              .isTakeOut(seatOptionsBySeatOptionName.getIsTakeOut())
              .regDateTime(waiting.getRegDateTime())
              .build();
        })
        .toList();
  }

  private Map<String, RemoteWaitingListOrderMenuDto> createOrderMap(List<String> waitingIds) {
    List<WaitingOrderDto> waitingOrderDtos = waitingOrderQueryRepository.findByWaitingIds(waitingIds);
    return waitingOrderDtos.stream()
        .collect(Collectors.toMap(
            WaitingOrderDto::getWaitingId,
            RemoteWaitingListOrderMenuDto::of,
            (item1, item2) -> item1)
        );
  }

  private Map<Long, String> createCustomerMap(List<WaitingEntity> waitings) {
    List<Long> customerSeqs = convert(waitings, WaitingEntity::getCustomerSeq);
    List<CustomerEntity> customers = customerService.findByIds(customerSeqs);
    return customers.stream()
        .collect(Collectors.toMap(CustomerEntity::getSeq, CustomerEntity::getLocalPhoneNumber));
  }

  private List<String> extractShopIdsFrom(List<WaitingEntity> waitings) {
    return convert(waitings, WaitingEntity::getShopId);
  }

  private Map<String, ShopDto> createShopDtoMap(List<String> shopIds) {
    List<ShopDto> shopDtos = shopQueryRepository.findByShopIds(shopIds);
    return convertToMap(shopDtos, ShopDto::getShopId);
  }

  private Map<String, String> createShopSeqMappings(List<String> shopIds) {
    List<ChannelMappingEntity> channelMappingByWaitingShopIds = channelService.getChannelMappingByWaitingShopIds(
        ServiceChannelId.CATCHTABLE_B2C.getValue(), shopIds);

    return channelMappingByWaitingShopIds.stream()
        .collect(Collectors.toMap(ChannelMappingEntity::getShopId,
            ChannelMappingEntity::getChannelShopId));
  }

  private Map<String, HomeSettingsData> createHomeSettingsDataMap(List<String> shopIds) {
    List<HomeSettingsEntity> homeSettingsEntities = homeSettingsService.findHomeSettings(shopIds);
    return homeSettingsEntities.stream()
        .collect(
            Collectors.toMap(HomeSettingsEntity::getShopId, HomeSettingsEntity::getHomeSettingsData,
                (item1, item2) -> item1));
  }

  private Map<String, Set<WaitingOrderCountDto>> createWaitingOrderMap(LocalDate operationDate,
      List<String> shopIds) {
    List<WaitingOrderCountDto> waitingOrderDtos = waitingCountQueryRepository.findAllWaitingOrdersShopIdsIn(
        shopIds, operationDate);
    return groupingBySet(waitingOrderDtos, WaitingOrderCountDto::getShopId);
  }

  private int calculateWaitingTeamCount(
      Map<String, Set<WaitingOrderCountDto>> groupingWaitingOrderMap, WaitingEntity waiting) {
    Set<WaitingOrderCountDto> waitingOrderDtos = groupingWaitingOrderMap.getOrDefault(
        waiting.getShopId(), Set.of());

    long waitingTeamCount = waitingOrderDtos.stream()
        .filter(dto -> waiting.isSameSeatOptionName(dto.getSeatOptionName()))
        .filter(dto -> waiting.isWaitingOrderGreaterOrEqualThan(dto.getWaitingOrder()))
        .count();
    return Long.valueOf(waitingTeamCount).intValue();
  }

  private Map<String, HomeSettingsEntity> createHomeSettingsMap(
      List<WaitingOfOtherShopQueryDto> waitings) {
    List<String> shopIds = convert(waitings, WaitingOfOtherShopQueryDto::getShopId);

    List<HomeSettingsEntity> homeSettings = homeSettingsQueryRepository.findByShopIds(shopIds);

    return homeSettings.stream()
        .collect(
            Collectors.toMap(HomeSettingsEntity::getShopId, item -> item, (item1, item2) -> item1));
  }

  private Integer getExpectedPeriodBySeatOption(String seatOptionName,
      HomeSettingsData homeSettingsData) {
    return homeSettingsData
        .getExpectedPeriodBySeatOption(seatOptionName);
  }

  private HomeSettingsEntity createDefaultHomeSettings(String shopId) {
    return new HomeSettingsEntity(shopId, DefaultHomeSettingDataFactory.create());
  }

}
