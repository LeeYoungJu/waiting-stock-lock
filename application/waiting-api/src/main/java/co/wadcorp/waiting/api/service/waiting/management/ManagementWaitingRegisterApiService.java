package co.wadcorp.waiting.api.service.waiting.management;

import static co.wadcorp.waiting.data.domain.customer.CustomerEntity.EMPTY_CUSTOMER_ENTITY;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.api.controller.waiting.management.dto.request.WaitingRegisterByManagerRequest;
import co.wadcorp.waiting.api.model.waiting.response.WaitingRegisterByManagerResponse;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.ShopCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.TermsCustomerEntity;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntities;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingRegisterValidator;
import co.wadcorp.waiting.data.event.ChangedMenuStockEvent;
import co.wadcorp.waiting.data.event.RegisteredEvent;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.service.customer.CustomerService;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.customer.TermsCustomerService;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.settings.OptionSettingsService;
import co.wadcorp.waiting.data.service.settings.OrderSettingsService;
import co.wadcorp.waiting.data.service.waiting.WaitingCustomerPhoneSimultaneousCheckService;
import co.wadcorp.waiting.data.service.waiting.WaitingNumberService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수기 등록은 테이블 설정 정책은 무시하고 등록할 수 있다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementWaitingRegisterApiService {

  private final WaitingService waitingService;
  private final CustomerService customerService;
  private final ShopCustomerService shopCustomerService;
  private final OptionSettingsService optionSettingsService;
  private final HomeSettingsService homeSettingsService;
  private final OrderSettingsService orderSettingsService;
  private final TermsCustomerService termsCustomerService;

  private final OrderService orderService;

  private final WaitingNumberService waitingNumberService;
  private final WaitingCustomerPhoneSimultaneousCheckService customerPhoneSimultaneousCheckService;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public WaitingRegisterByManagerResponse registerByManager(String shopId, LocalDate operationDate,
      WaitingRegisterByManagerRequest request, String deviceId) {
    HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(shopId)
        .getHomeSettingsData();
    OptionSettingsData optionSettings = optionSettingsService.getOptionSettings(shopId)
        .getOptionSettingsData();
    OrderSettingsData orderSettings = orderSettingsService.getOrderSettings(shopId)
        .getOrderSettingsData();

    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(request.getPhoneNumber());
    checkSimultaneousRegister(shopId, operationDate, phoneNumber);

    // 고객 정보 조회 - 없다면 저장도 진행
    ShopCustomerEntity shopCustomer = getShopCustomer(shopId, phoneNumber, request.getName());
    shopCustomer.updateVisitCount();

    List<WaitingEntity> waitingList = waitingService.getWaitingByCustomerSeqToday(
        shopCustomer.getCustomerSeq(),
        operationDate
    );

    // 중복 웨이팅 검증
    WaitingRegisterValidator.validate(shopId, waitingList);

    // 좌석 이름으로 매장이용방식(좌석옵션) 조회
    String seatOptionId = request.getSeatOption().getId();
    SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionId(seatOptionId);

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
        operationDate,
        shopCustomer.getName(), homeSettings, optionSettings, waitingNumbers,
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
        savedWaitingHistory.getSeq(),
        shopCustomer.getCustomerSeq());
    termsCustomerService.saveAllTermsCustomer(termsCustomerEntities);

    eventPublisher.publishEvent(
        new RegisteredEvent(shopId, savedWaitingHistory.getSeq(), operationDate, deviceId));

    log.info("웨이팅 등록 - 수기, shopId: {}, operationDate: {}, customerSeq: {}, waitingId: {}",
        savedWaitingHistory.getShopId(),
        savedWaitingHistory.getOperationDate(),
        savedWaitingHistory.getCustomerSeq(),
        savedWaitingHistory.getWaitingId()
    );

    return WaitingRegisterByManagerResponse.builder()
        .waitingId(savedWaitingHistory.getWaitingId())
        .waitingNumber(savedWaitingHistory.getWaitingNumber()).build();

  }

  private void checkSimultaneousRegister(String shopId, LocalDate operationDate, PhoneNumber phoneNumber) {
    if (phoneNumber != null
        && phoneNumber.isValid()
        && customerPhoneSimultaneousCheckService.isSimultaneous(phoneNumber, shopId, operationDate)
    ) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_REGISTERED_WAITING);
    }
  }

  private ShopCustomerEntity getShopCustomer(String shopId, PhoneNumber phoneNumber,
      String customerName) {
    if (phoneNumber == null || !phoneNumber.isValid()) {
      return ShopCustomerEntity.ofName(customerName);
    }

    // 고객 정보 조회 & 저장
    CustomerEntity customer = customerService.getCustomerByCustomerPhone(phoneNumber);

    if (customer == EMPTY_CUSTOMER_ENTITY) {
      customer = customerService.saveCustomerEntity(CustomerEntity.create(phoneNumber));
    }

    // shop_customer 저장
    ShopCustomerEntity shopCustomer = shopCustomerService.getShopCustomerById(shopId,
        customer.getSeq(), customerName);
    return shopCustomerService.saveShopCustomer(shopCustomer);
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

    // 예상 시간을 사용하지 않는다면 Null
    Integer expectedWaitingPeriod = seatOptions.calculateExpectedWaitingPeriod(
        registerWaitingOrder);

    if (Objects.isNull(expectedWaitingPeriod)) {
      return null;
    }
    ZonedDateTime now = ZonedDateTimeUtils.nowOfSeoul();
    return now.plusMinutes(expectedWaitingPeriod);
  }
}
