package co.wadcorp.waiting.api.internal.service.waiting;

import static co.wadcorp.waiting.data.domain.customer.CustomerEntity.EMPTY_CUSTOMER_ENTITY;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingRegisterServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.AdditionalOptionDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.CreatedOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.PersonOptionDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse.RemoteInvalidMenu;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingRegisterResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.TableDto;
import co.wadcorp.waiting.api.internal.service.waiting.validator.RemoteOrderMenuStockValidator;
import co.wadcorp.waiting.api.internal.service.waiting.validator.RemoteOrderMenuValidator;
import co.wadcorp.waiting.api.internal.service.waiting.validator.RemoteOrderSettingsValidator;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.service.waiting.ShopOperationApiService;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.AdditionalOption;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.validator.MenuStockValidator;
import co.wadcorp.waiting.data.domain.stock.validator.exception.StockException;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntities;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingRegisterValidator;
import co.wadcorp.waiting.data.event.ChangedMenuStockEvent;
import co.wadcorp.waiting.data.event.RegisteredEvent;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.menu.MenuService;
import co.wadcorp.waiting.data.service.order.OrderSaveService;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.settings.OptionSettingsService;
import co.wadcorp.waiting.data.service.settings.OrderSettingsService;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.data.service.stock.StockService;
import co.wadcorp.waiting.data.service.waiting.WaitingCustomerPhoneSimultaneousCheckService;
import co.wadcorp.waiting.data.service.waiting.WaitingNumberService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RemoteWaitingRegisterApiService {

  private static final int TAKE_OUT_PERSON_COUNT = 1;
  private static final String CATCH_TABLE_APP = "CATCH_TABLE_APP";

  private final WaitingService waitingService;
  private final CustomerService customerService;
  private final ShopCustomerService shopCustomerService;
  private final ShopService shopService;
  private final ShopOperationApiService shopOperationApiService;
  private final OptionSettingsService optionSettingsService;
  private final HomeSettingsService homeSettingsService;
  private final WaitingNumberService waitingNumberService;
  private final WaitingCustomerPhoneSimultaneousCheckService customerPhoneSimultaneousCheckService;
  private final OrderSaveService orderService;
  private final MenuService menuService;
  private final StockService stockService;
  private final OrderSettingsService orderSettingsService;

  private final WaitingCountQueryRepository waitingCountQueryRepository;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public RemoteWaitingRegisterResponse register(
      ChannelShopIdMapping channelShopIdMapping,
      LocalDate operationDate,
      ZonedDateTime nowDateTime,
      RemoteWaitingRegisterServiceRequest request
  ) {
    channelShopIdMapping.checkOnlyOneShopId();
    String waitingShopId = channelShopIdMapping.getFirstWaitingShopId();

    ShopOperationInfoEntity shopOperationInfoEntity =
        shopOperationApiService.findByShopIdAndOperationDate(waitingShopId, operationDate);

    if (OperationStatus.findWithRemoteTime(shopOperationInfoEntity, nowDateTime)
        != OperationStatus.OPEN) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_OPEN_WAITING_OPERATION);
    }

    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(request.getPhoneNumber());
    checkSimultaneousRegister(waitingShopId, operationDate, phoneNumber);

    // 고객 정보 조회 - 없다면 저장도 진행
    ShopCustomerEntity shopCustomer = getShopCustomer(waitingShopId, phoneNumber);
    shopCustomer.updateVisitCount();

    // 중복 웨이팅 검증
    List<WaitingEntity> waitingList = waitingService.getWaitingByCustomerSeqToday(
        shopCustomer.getCustomerSeq(),
        operationDate);
    WaitingRegisterValidator.validate(waitingShopId, waitingList);

    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(waitingShopId)
        .getHomeSettingsData();
    OptionSettingsData optionSettings = optionSettingsService.getOptionSettings(waitingShopId)
        .getOptionSettingsData();

    // 좌석 이름으로 매장이용방식(좌석옵션) 조회
    SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionId(request.getTableId());

    // 총 착석 인원 (인원옵션설정 사용 매장은 비착석 인원 제외)
    Integer totalPersonCount = getTotalSeatCountByPersonOption(
        request.getTotalPersonCount(),
        request.getPersonOptions(),
        optionSettings,
        seatOptions
    );

    // 착석 인원 검증
    seatOptions.validSeatCount(totalPersonCount);

    WaitingEntities waitingEntities = new WaitingEntities(
        waitingService.findAllByShopIdAndOperationDate(waitingShopId, operationDate)
    );

    // 선주문 정보 검증
    if(Objects.nonNull(request.getOrder()) && request.getOrder().haveMenus()) {
      validateOrderSettings(waitingShopId);
      validateOrderMenus(request.getOrder());
      validateOrderMenuStock(operationDate, request.getOrder());
    }

    // 예상 입장 시각
    ZonedDateTime expectedSittingDateTime = getExpectedSittingDateTime(waitingEntities,
        seatOptions);

    // 웨이팅 채번 부여
    WaitingNumber waitingNumbers = waitingNumberService.getWaitingNumber(waitingShopId,
        operationDate);

    // 웨이팅 및 히스토리 저장
    WaitingEntity waitingEntity = WaitingEntity.builder()
        .shopId(waitingShopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .customerSeq(shopCustomer.getCustomerSeq())
        .customerName(shopCustomer.getName())
        .operationDate(operationDate)
        .registerChannel(RegisterChannel.CATCH_APP)
        .waitingNumbers(waitingNumbers)
        .waitingStatus(WaitingStatus.WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .totalPersonCount(totalPersonCount)
        .seatOptionName(seatOptions.getName())
        .personOptionsData(convertPersonOptionsData(request.getPersonOptions(), optionSettings))
        .expectedSittingDateTime(expectedSittingDateTime)
        .build();

    WaitingHistoryEntity savedWaitingHistory = waitingService.saveWaiting(waitingEntity);

    // 주문 저장
    OrderType orderType = OrderType.findBy(seatOptions.getIsTakeOut());
    CreatedOrderDto savedOrderDto = saveOrder(request.getOrder(), orderType, waitingShopId,
        waitingEntity.getWaitingId(), operationDate);

    int waitingTeamCount = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
        waitingEntity.getShopId(),
        operationDate, waitingEntity.getWaitingOrder(), waitingEntity.getSeatOptionName());

    eventPublisher.publishEvent(
        new RegisteredEvent(waitingShopId, savedWaitingHistory.getSeq(), operationDate,
            CATCH_TABLE_APP));

    return RemoteWaitingRegisterResponse.builder()
        .id(savedWaitingHistory.getWaitingId())
        .shopId(Long.valueOf(channelShopIdMapping.getChannelShopId(waitingShopId)))
        .shopName(shopService.findByShopId(waitingShopId).getShopName())
        .registerChannel(savedWaitingHistory.getRegisterChannel())
        .operationDate(savedWaitingHistory.getOperationDate())
        .customerPhoneNumber(request.getPhoneNumber())
        .waitingNumber(savedWaitingHistory.getWaitingNumber())
        .waitingOrder(waitingTeamCount)
        .waitingRegisteredOrder(savedWaitingHistory.getWaitingOrder())
        .waitingStatus(savedWaitingHistory.getWaitingStatus())
        .waitingDetailStatus(savedWaitingHistory.getWaitingDetailStatus())
        .totalPersonCount(savedWaitingHistory.getTotalPersonCount())
        .expectedSittingDateTime(expectedSittingDateTime)
        .waitingCompleteDateTime(savedWaitingHistory.getWaitingCompleteDateTime())
        .personOptions(
            convertPersonOptions(savedWaitingHistory.getPersonOptionsData())
        )
        .table(TableDto.builder()
            .id(seatOptions.getId())
            .name(seatOptions.getName())
            .isTakeOut(seatOptions.getIsTakeOut())
            .build()
        )
        .order(savedOrderDto)
        .regDateTime(savedWaitingHistory.getRegDateTime())
        .build();
  }

  private CreatedOrderDto saveOrder(RemoteOrderDto orderDto,
      OrderType orderType, String shopId, String waitingId, LocalDate operationDate) {
    if (Objects.isNull(orderDto) || orderDto.haveNoMenu()) {
      // 웨이팅 등록 reqeust에 주문정보가 없으면 저장하지 않고 response에 넣을 EMPTY 인스턴스를 반환한다.
      return CreatedOrderDto.EMPTY;
    }

    OrderEntity orderEntity = orderDto.toEntity(shopId, waitingId,
        operationDate, orderType, orderDto);
    OrderEntity savedOrderEntity = orderService.save(orderEntity);

    eventPublisher.publishEvent(new ChangedMenuStockEvent(shopId, "INTERNAL_API"));

    return CreatedOrderDto.of(savedOrderEntity);
  }

  // 선주문 설정 검증
  private void validateOrderSettings(String shopId) {
    OrderSettingsData orderSettings = orderSettingsService.getOrderSettings(shopId)
        .getOrderSettingsData();
    RemoteOrderSettingsValidator.validate(orderSettings);
  }

  // 요청한 주문의 메뉴 검증
  private void validateOrderMenus(RemoteOrderDto orderDto) {
    RemoteOrderMenuValidator.validate(orderDto);
  }

  // 요청한 주문의 메뉴 재고 검증
  private void validateOrderMenuStock(LocalDate operationDate,
      RemoteOrderDto orderDto
  ) {
    List<String> menuIds = orderDto.getMenuIds();

    Map<String, MenuEntity> menuEntityMap = menuService.getMenuIdMenuEntityMap(menuIds);
    Map<String, StockEntity> menuStockMap = stockService.getMenuIdMenuStockMap(operationDate,
        menuIds);

    RemoteOrderMenuStockValidator.validate(orderDto, menuEntityMap, menuStockMap);
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

  private PersonOptionsData convertPersonOptionsData(
      List<RemoteWaitingRegisterServiceRequest.PersonOptionVO> personOptions,
      OptionSettingsData optionSettings) {
    return PersonOptionsData.builder()
        .personOptions(convertPersonOptions(personOptions, optionSettings))
        .build();
  }

  private List<PersonOption> convertPersonOptions(
      List<RemoteWaitingRegisterServiceRequest.PersonOptionVO> personOptions,
      OptionSettingsData optionSettings) {
    return personOptions.stream()
        .map(o -> {
          PersonOptionSetting personOption = optionSettings.findPersonOption(o.getId());
          List<PersonOption.AdditionalOption> additionalOptionList = o.getAdditionalOptions()
              .stream()
              .map(item -> {
                AdditionalOption additionalOption = personOption.findAdditionalOption(item.getId());
                return PersonOption.AdditionalOption.builder()
                    .name(additionalOption.getName())
                    .count(item.getCount())
                    .build();
              })
              .toList();

          return new PersonOption(personOption.getName(), o.getCount(), additionalOptionList);
        })
        .toList();
  }

  private void checkSimultaneousRegister(String shopId, LocalDate operationDate,
      PhoneNumber phoneNumber) {
    if (phoneNumber != null
        && phoneNumber.isValid()
        && customerPhoneSimultaneousCheckService.isSimultaneous(phoneNumber, shopId, operationDate)
    ) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_REGISTERED_WAITING);
    }
  }

  private ShopCustomerEntity getShopCustomer(String shopId, PhoneNumber phoneNumber) {
    // 고객 정보 조회 & 저장
    CustomerEntity customer = customerService.getCustomerByCustomerPhone(phoneNumber);

    if (customer == EMPTY_CUSTOMER_ENTITY) {
      customer = customerService.saveCustomerEntity(CustomerEntity.create(phoneNumber));
    }

    // shop_customer 저장
    ShopCustomerEntity shopCustomer = shopCustomerService.getShopCustomerById(shopId,
        customer.getSeq());
    return shopCustomerService.saveShopCustomer(shopCustomer);
  }


  private Integer getTotalSeatCountByPersonOption(
      Integer totalPersonCount,
      List<RemoteWaitingRegisterServiceRequest.PersonOptionVO> personOptions,
      OptionSettingsData optionSettings,
      SeatOptions seatOptions
  ) {

    if (seatOptions.getIsTakeOut()) {
      return TAKE_OUT_PERSON_COUNT;
    }

    if (optionSettings.isNotUsePersonOptionSetting()) {
      return totalPersonCount;
    }

    return optionSettings.getPersonOptionSettings().stream()
        .filter(PersonOptionSetting::getIsSeat)
        .map(e -> personOptions.stream()
            .filter(r -> StringUtils.equals(r.getId(), e.getId()))
            .findFirst()
            .map(RemoteWaitingRegisterServiceRequest.PersonOptionVO::getCount).orElse(0))
        .mapToInt(Integer::valueOf)
        .sum();
  }

  /**
   * 예상 대기시간(m) = 팀당 예상 대기시간 * (남은 웨이팅 팀 수 + 1)
   * <p>
   * (남은 웨이팅 팀 수 + 1) 은 등록시점 웨이팅 순번과 동일하다.
   */
  private ZonedDateTime getExpectedSittingDateTime(WaitingEntities waitingEntities,
      SeatOptions seatOptions) {
    if (seatOptions.isNotUseExpectedWaitingPeriod()) {
      return null;
    }

    int registerWaitingOrder = waitingEntities.getRegisterWaitingOrder(seatOptions.getName()) + 1;
    Integer expectedWaitingPeriod = seatOptions.calculateExpectedWaitingPeriod(
        registerWaitingOrder);

    ZonedDateTime now = ZonedDateTime.now();
    return now.plusMinutes(expectedWaitingPeriod);
  }
}
