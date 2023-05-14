package co.wadcorp.waiting.api.service.waiting;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.waiting.data.domain.customer.CustomerEntity.EMPTY_CUSTOMER_ENTITY;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.api.controller.waiting.register.dto.request.WaitingRegisterRequest;
import co.wadcorp.waiting.api.model.waiting.request.CancelWaitingRequest;
import co.wadcorp.waiting.api.model.waiting.request.PageRequestParams;
import co.wadcorp.waiting.api.model.waiting.response.MyWaitingInfoResponse;
import co.wadcorp.waiting.api.model.waiting.response.RegisterWaitingListResponse;
import co.wadcorp.waiting.api.model.waiting.response.RegisterWaitingListResponse.RegisterWaitingDto;
import co.wadcorp.waiting.api.model.waiting.response.WaitingRegisterResponse;
import co.wadcorp.waiting.api.model.waiting.vo.PageVO;
import co.wadcorp.waiting.api.model.waiting.vo.PersonOptionVO;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterValidateMenuResponse;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterValidateMenuResponse.InvalidMenu;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.TermsCustomerEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.validator.MenuStockValidator;
import co.wadcorp.waiting.data.domain.stock.validator.exception.StockException;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntities;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingRegisterValidator;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.ChangedMenuStockEvent;
import co.wadcorp.waiting.data.event.RegisteredEvent;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.query.waiting.WaitingPageListQueryRepository;
import co.wadcorp.waiting.data.query.waiting.WaitingRegisterQueryRepository;
import co.wadcorp.waiting.data.query.waiting.dto.MyWaitingInfoDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOnRegistrationDto;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.customer.TermsCustomerService;
import co.wadcorp.waiting.data.service.menu.MenuService;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.settings.OptionSettingsService;
import co.wadcorp.waiting.data.service.settings.OrderSettingsService;
import co.wadcorp.waiting.data.service.stock.StockService;
import co.wadcorp.waiting.data.service.waiting.WaitingChangeStatusService;
import co.wadcorp.waiting.data.service.waiting.WaitingCustomerPhoneSimultaneousCheckService;
import co.wadcorp.waiting.data.service.waiting.WaitingNumberService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingRegisterApiService {

  private static final int TAKE_OUT_PERSON_COUNT = 1;

  private final WaitingService waitingService;
  private final WaitingChangeStatusService waitingChangeStatusService;
  private final CustomerService customerService;
  private final ShopCustomerService shopCustomerService;
  private final OptionSettingsService optionSettingsService;
  private final HomeSettingsService homeSettingsService;
  private final OrderSettingsService orderSettingsService;
  private final TermsCustomerService termsCustomerService;

  private final MenuService menuService;
  private final OrderService orderService;
  private final StockService stockService;

  private final WaitingRegisterQueryRepository waitingRegisterQueryRepository;
  private final WaitingPageListQueryRepository waitingPageListQueryRepository;
  private final WaitingNumberService waitingNumberService;
  private final WaitingCustomerPhoneSimultaneousCheckService customerPhoneSimultaneousCheckService;

  private final ApplicationEventPublisher eventPublisher;

  public void checkWaitingBeforeRegisterByPhone(String shopId, String customerPhone,
      LocalDate operationDate) {
    PhoneNumber encCustomerPhone = PhoneNumberUtils.ofKr(customerPhone);

    CustomerEntity customer = customerService.getCustomerByCustomerPhone(encCustomerPhone);
    if (customer == EMPTY_CUSTOMER_ENTITY) {
      return;
    }
    // 중복 웨이팅 검증
    List<WaitingEntity> waitingList = waitingService.getWaitingByCustomerSeqToday(
        customer.getSeq(),
        operationDate);
    WaitingRegisterValidator.validate(shopId, waitingList);
  }

  @Transactional
  public WaitingRegisterResponse registerWaiting(String shopId, LocalDate operationDate,
      WaitingRegisterRequest request, String deviceId) {

    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(request.getCustomerPhone());
    checkSimultaneousRegister(shopId, operationDate, phoneNumber);

    // 고객 정보 조회 - 없다면 저장도 진행
    ShopCustomerEntity shopCustomer = getShopCustomer(shopId, phoneNumber);
    shopCustomer.updateVisitCount();

    List<WaitingEntity> waitingList = waitingService.getWaitingByCustomerSeqToday(
        shopCustomer.getCustomerSeq(),
        operationDate
    );

    // 중복 웨이팅 검증
    WaitingRegisterValidator.validate(shopId, waitingList);

    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(shopId)
        .getHomeSettingsData();
    OptionSettingsData optionSettings = optionSettingsService.getOptionSettings(shopId)
        .getOptionSettingsData();
    OrderSettingsData orderSettings = orderSettingsService.getOrderSettings(shopId)
        .getOrderSettingsData();

    // 좌석 이름으로 매장이용방식(좌석옵션) 조회
    String seatOptionId = request.getSeatOption().getId();
    SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionId(seatOptionId);

    // 총 착석 인원 (인원옵션설정 사용 매장은 비착석 인원 제외)
    Integer totalSeatCount = getTotalSeatCountByPersonOption(
        request.getTotalPersonCount(),
        request.getPersonOptions(),
        optionSettings,
        seatOptions);

    // 착석 인원 검증
    validSeatCount(totalSeatCount, seatOptions);

    // 선주문 메뉴 검증
    if (orderSettings.isPossibleOrder() && Objects.nonNull(request.getOrder())) {
      List<String> menuIds = request.getMenuIds();
      List<MenuQuantity> menuQuantities = request.toMenuQuantity();

      validateOrderMenuStock(operationDate, menuIds, menuQuantities);
    }

    WaitingEntities waitingEntities = new WaitingEntities(
        waitingService.findAllByShopIdAndOperationDate(shopId, operationDate)
    );

    // 예상 입장 시각
    ZonedDateTime expectedSittingDateTime = getExpectedSittingDateTime(waitingEntities,
        seatOptions);

    // 웨이팅 채번 부여
    WaitingNumber waitingNumbers = waitingNumberService.getWaitingNumber(shopId, operationDate);

    // 웨이팅 및 히스토리 저장
    WaitingEntity waitingEntity = request.toWaitingEntity(shopId, shopCustomer.getCustomerSeq(),
        operationDate, shopCustomer.getName(), homeSettings, optionSettings, waitingNumbers,
        expectedSittingDateTime);
    WaitingHistoryEntity savedWaitingHistory = waitingService.saveWaiting(waitingEntity);

    // 주문 저장
    if (orderSettings.isPossibleOrder() && Objects.nonNull(request.getOrder())) {
      OrderType orderType = seatOptions.getIsTakeOut() ? OrderType.TAKE_OUT : OrderType.SHOP;
      OrderEntity orderEntity = request.toOrderEntity(shopId, waitingEntity.getWaitingId(),
          operationDate, orderType);
      orderService.save(orderEntity);
      eventPublisher.publishEvent(new ChangedMenuStockEvent(shopId, deviceId));
    }

    // 약관동의 내역 저장
    List<TermsCustomerEntity> termsCustomerEntities = request.toTermsCustomerEntities(shopId,
        savedWaitingHistory.getSeq(), shopCustomer.getCustomerSeq());
    termsCustomerService.saveAllTermsCustomer(termsCustomerEntities);

    eventPublisher.publishEvent(
        new RegisteredEvent(shopId, savedWaitingHistory.getSeq(), operationDate, deviceId));

    log.info("웨이팅 등록 - 현장, shopId: {}, operationDate: {}, customerSeq: {}, waitingId: {}",
        savedWaitingHistory.getShopId(),
        savedWaitingHistory.getOperationDate(),
        savedWaitingHistory.getCustomerSeq(),
        savedWaitingHistory.getWaitingId()
    );

    return WaitingRegisterResponse.builder()
        .waitingId(savedWaitingHistory.getWaitingId())
        .waitingNumber(savedWaitingHistory.getWaitingNumber())
        .build();
  }

  // 선주문 재고 검증 validate
  private void validateOrderMenuStock(LocalDate operationDate,
      List<String> menuIds,
      List<MenuQuantity> menuQuantities
  ) {
    Map<String, MenuEntity> menuEntityMap = menuService.getMenuIdMenuEntityMap(menuIds);
    Map<String, StockEntity> menuStockMap = stockService.getMenuIdMenuStockMap(
        operationDate, menuIds);

    try {
      MenuStockValidator.validateExceedingOrderQuantityPerTeam(menuQuantities, menuEntityMap,
          menuStockMap
      );
      MenuStockValidator.validateOutOfStock(menuQuantities, menuEntityMap, menuStockMap);

    } catch (StockException e) {
      ErrorCode errorCode = e.getErrorCode();
      List<InvalidStockMenu> invalidMenus = e.getInvalidMenus();

      throw new AppException(
          HttpStatus.BAD_REQUEST,
          errorCode.getMessage(),
          errorCode.getMessage(),
          RegisterValidateMenuResponse.builder()
              .reason(errorCode.getCode())
              .menus(getMenus(invalidMenus))
              .build()
      );
    }
  }

  @Transactional
  public void cancelWaitingByIdList(CancelWaitingRequest request, LocalDate operationDate, String deviceId) {
    List<String> waitingIds = request.getWaitingIdList();
    waitingIds.forEach(id -> {
      WaitingHistoryEntity waitingHistory = waitingChangeStatusService.cancelByCustomer(id);
      shopCustomerService.cancel(waitingHistory.getShopId(), waitingHistory.getCustomerSeq());

      eventPublisher.publishEvent(
          new CanceledByCustomerEvent(waitingHistory.getShopId(), waitingHistory.getSeq(),
              operationDate, deviceId));
    });
  }

  public MyWaitingInfoResponse getShopCustomerWaitingInfo(String shopId, String customerPhone) {
    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(customerPhone);
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    CustomerEntity customer = customerService.findByCustomerPhone(phoneNumber);
    MyWaitingInfoDto waiting = waitingRegisterQueryRepository.getWaitingByShopIdAndCustomerSeqToday(
            shopId, operationDate, customer.getSeq())
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_WAITING));
    return MyWaitingInfoResponse.toDto(waiting);
  }

  public RegisterWaitingListResponse getAllWaiting(String shopId, PageRequestParams request,
      LocalDate operationDate) {
    Pageable pageable = PageRequest.of(request.pageByPageRequest(), request.getLimit());
    Page<WaitingOnRegistrationDto> waitingDtos = waitingPageListQueryRepository.getWaitingListOnRegistration(
        shopId, operationDate, pageable);

    return RegisterWaitingListResponse.builder()
        .waiting(convert(waitingDtos.getContent(), RegisterWaitingDto::toDto))
        .page(new PageVO(waitingDtos.getTotalElements(), request.getPage(), pageable.getPageSize()))
        .build();
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

    if (Objects.isNull(expectedWaitingPeriod)) {
      return null;
    }
    ZonedDateTime now = ZonedDateTimeUtils.nowOfSeoul();
    return now.plusMinutes(expectedWaitingPeriod);
  }

  /**
   * 착석 인원 검증
   */
  private void validSeatCount(Integer totalSeatCount, SeatOptions seatOptions) {
    seatOptions.validSeatCount(totalSeatCount);
  }

  private Integer getTotalSeatCountByPersonOption(
      Integer totalPersonCount,
      List<PersonOptionVO> personOptions,
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
            .map(PersonOptionVO::getCount).orElse(0))
        .mapToInt(Integer::valueOf)
        .sum();
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

  private List<InvalidMenu> getMenus(List<InvalidStockMenu> invalidMenus) {
    return invalidMenus.stream()
        .map(item -> InvalidMenu.builder()
            .id(item.getMenuId())
            .name(item.getName())
            .quantity(item.getQuantity())
            .remainingQuantity(item.getRemainingQuantity())
            .build())
        .toList();
  }

}
