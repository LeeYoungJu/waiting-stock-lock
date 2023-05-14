package co.wadcorp.waiting.api.internal.service.waiting;

import static co.wadcorp.waiting.data.domain.waiting.RegisterChannel.WAITING_APP;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.CANCEL;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.EXPIRATION;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.SITTING;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingCheckBeforeRegisterServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingListServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteListWaitingResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingListOrderMenuDto;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.CustomerRepository;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemRepository;
import co.wadcorp.waiting.data.domain.order.OrderLineItemStatus;
import co.wadcorp.waiting.data.domain.order.OrderRepository;
import co.wadcorp.waiting.data.domain.order.OrderStatus;
import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.infra.channel.JpaChannelMappingRepository;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteWaitingApiServiceTest extends IntegrationTest {

  @Autowired
  private WaitingRepository waitingRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private JpaChannelMappingRepository jpaChannelMappingRepository;
  @Autowired
  private ShopRepository shopRepository;
  @Autowired
  private HomeSettingsRepository homeSettingsRepository;
  @Autowired
  private RemoteWaitingApiService remoteWaitingApiService;
  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private OrderLineItemRepository orderLineItemRepository;

  @DisplayName("주어진 웨이팅 ID 리스트로 웨이팅을 조회한다.")
  @Test
  void findWaitings() {
    // given
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 3, 13, 10, 0));
    LocalDate operationDate = LocalDate.of(2023, 3, 13);

    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    createShop(shopId1);
    createShop(shopId2);
    createChannelMapping("11", shopId1);
    createChannelMapping("12", shopId2);

    createHomeSettings(shopId1, "DEFAULT");
    createHomeSettings(shopId2, "TABLE"); // 모드 상관 없이 조회 가능해야 한다.

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, 9, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, 10, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting3 = createWaiting(shopId1, operationDate, 11, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting4 = createWaiting(shopId2, operationDate, 10, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting5 = createWaiting(shopId1, operationDate.minusDays(1), 10, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting6 = createWaiting(shopId1, operationDate, 10, "바", WAITING,
        WaitingDetailStatus.WAITING);

    RemoteWaitingListServiceRequest request = RemoteWaitingListServiceRequest.builder()
        .waitingIds(List.of(
            waiting1.getWaitingId(), waiting2.getWaitingId(), waiting3.getWaitingId(),
            waiting4.getWaitingId(), waiting5.getWaitingId(), waiting6.getWaitingId()
        ))
        .operationDate(operationDate)
        .build();

    // when
    List<RemoteListWaitingResponse> results = remoteWaitingApiService.findWaitings(request,
        nowDateTime);

    // then
    assertThat(results).hasSize(5)
        .extracting("registerChannel", "operationDate", "customerPhoneNumber",
            "waitingOrder", "waitingRegisteredOrder", "waitingStatus",
            "waitingDetailStatus", "totalPersonCount"
        )
        .containsExactlyInAnyOrder(
            tuple(WAITING_APP, ISO8601.formatAsDate(operationDate), null, 1, 9, WAITING,
                WaitingDetailStatus.WAITING, 1),
            tuple(WAITING_APP, ISO8601.formatAsDate(operationDate), null, 2, 10, WAITING,
                WaitingDetailStatus.WAITING, 1),
            tuple(WAITING_APP, ISO8601.formatAsDate(operationDate), null, 3, 11, WAITING,
                WaitingDetailStatus.WAITING, 1),
            tuple(WAITING_APP, ISO8601.formatAsDate(operationDate), null, 1, 10, WAITING,
                WaitingDetailStatus.WAITING, 1),
            tuple(WAITING_APP, ISO8601.formatAsDate(operationDate), null, 1, 10, WAITING,
                WaitingDetailStatus.WAITING, 1)
        );
  }

  @DisplayName("완료된 웨이팅은 앞 팀 수, 대기예상시간, 테이블정보 등을 제공하지 않는다.")
  @Test
  void findLastWaitings() {
    // given
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 3, 13, 10, 0));
    LocalDate operationDate = LocalDate.of(2023, 3, 13);

    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    createShop(shopId1);
    createShop(shopId2);
    createChannelMapping("11", shopId1);
    createChannelMapping("12", shopId2);

    createHomeSettings(shopId1, "DEFAULT");
    createHomeSettings(shopId2, "TABLE"); // 모드 상관 없이 조회 가능해야 한다.

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, 10, "홀", SITTING,
        WaitingDetailStatus.SITTING);
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, 11, "홀", CANCEL,
        WaitingDetailStatus.CANCEL_BY_CUSTOMER);
    WaitingEntity waiting3 = createWaiting(shopId2, operationDate, 10, "홀", EXPIRATION,
        WaitingDetailStatus.EXPIRATION);

    RemoteWaitingListServiceRequest request = RemoteWaitingListServiceRequest.builder()
        .waitingIds(List.of(
            waiting1.getWaitingId(), waiting2.getWaitingId(), waiting3.getWaitingId()
        ))
        .operationDate(operationDate)
        .build();

    // when
    List<RemoteListWaitingResponse> results = remoteWaitingApiService.findWaitings(request,
        nowDateTime);

    // then
    assertThat(results).hasSize(3)
        .extracting("operationDate", "waitingOrder", "waitingRegisteredOrder",
            "waitingStatus", "waitingDetailStatus", "expectedSittingDateTime", "table"
        )
        .containsExactlyInAnyOrder(
            tuple(ISO8601.formatAsDate(operationDate), null, 10, SITTING,
                WaitingDetailStatus.SITTING, null, null),
            tuple(ISO8601.formatAsDate(operationDate), null, 11, CANCEL,
                WaitingDetailStatus.CANCEL_BY_CUSTOMER, null, null),
            tuple(ISO8601.formatAsDate(operationDate), null, 10, EXPIRATION,
                WaitingDetailStatus.EXPIRATION, null, null)
        );
  }

  @DisplayName("원격 웨이팅 목록 조회 시 주문이 존재하는 웨이팅은 주문정보도 같이 제공한다.")
  @Test
  void findWaitingsWithOrder() {
    // given
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(LocalDateTime.of(2023, 3, 13, 10, 0));
    LocalDate operationDate = LocalDate.of(2023, 3, 13);

    String shopId1 = "shopId-1";
    String shopId2 = "shopId-2";
    createShop(shopId1);
    createShop(shopId2);
    createChannelMapping("11", shopId1);
    createChannelMapping("12", shopId2);

    createHomeSettings(shopId1, "DEFAULT");
    createHomeSettings(shopId2, "TABLE"); // 모드 상관 없이 조회 가능해야 한다.

    WaitingEntity waiting1 = createWaiting(shopId1, operationDate, 9, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting2 = createWaiting(shopId1, operationDate, 10, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting3 = createWaiting(shopId1, operationDate, 11, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting4 = createWaiting(shopId2, operationDate, 10, "홀", WAITING,
        WaitingDetailStatus.WAITING);
    WaitingEntity waiting5 = createWaiting(shopId1, operationDate, 10, "바", WAITING,
        WaitingDetailStatus.WAITING);

    OrderEntity orderEntity1 = createOrder(shopId1, waiting1.getWaitingId(), operationDate,
        OrderType.SHOP, BigDecimal.valueOf(16000));
    OrderEntity orderEntity2 = createOrder(shopId1, waiting2.getWaitingId(), operationDate,
        OrderType.SHOP, BigDecimal.valueOf(12000));
    OrderEntity orderEntity3 = createOrder(shopId2, waiting4.getWaitingId(), operationDate,
        OrderType.SHOP, BigDecimal.valueOf(8000));
    OrderEntity orderEntity4 = createOrder(shopId1, waiting5.getWaitingId(), operationDate,
        OrderType.SHOP, BigDecimal.valueOf(19000));

    createOrderLineItem(orderEntity1.getOrderId(), "menuId-1-1", "메뉴1-1",
        BigDecimal.valueOf(10000), BigDecimal.valueOf(10000), 1);
    createOrderLineItem(orderEntity1.getOrderId(), "menuId-1-2", "메뉴1-2",
        BigDecimal.valueOf(3000), BigDecimal.valueOf(6000), 2);
    createOrderLineItem(orderEntity2.getOrderId(), "menuId-2-1", "메뉴2-1",
        BigDecimal.valueOf(12000), BigDecimal.valueOf(12000), 1);
    createOrderLineItem(orderEntity3.getOrderId(), "menuId-3-1", "메뉴3-1",
        BigDecimal.valueOf(500), BigDecimal.valueOf(1000), 2);
    createOrderLineItem(orderEntity3.getOrderId(), "menuId-3-2", "메뉴3-2",
        BigDecimal.valueOf(3500), BigDecimal.valueOf(7000), 2);
    createOrderLineItem(orderEntity4.getOrderId(), "menuId-4-1", "메뉴4-1",
        BigDecimal.valueOf(19000), BigDecimal.valueOf(19000), 1);

    RemoteWaitingListServiceRequest request = RemoteWaitingListServiceRequest.builder()
        .waitingIds(List.of(
            waiting1.getWaitingId(), waiting2.getWaitingId(), waiting3.getWaitingId(),
            waiting4.getWaitingId(), waiting5.getWaitingId()
        ))
        .operationDate(operationDate)
        .build();

    // when
    List<RemoteListWaitingResponse> results = remoteWaitingApiService.findWaitings(request,
        nowDateTime);

    // then
    List<RemoteWaitingListOrderMenuDto> orders = results.stream()
        .map(RemoteListWaitingResponse::getOrder)
        .toList();

    assertThat(orders).hasSize(5)
        .extracting("id")
        .contains(
            orderEntity1.getOrderId(),
            orderEntity2.getOrderId(),
            orderEntity3.getOrderId(),
            orderEntity4.getOrderId(),
            ""
        );

    // 웨이팅1은 메뉴가 2개
    RemoteListWaitingResponse waiting1Result = results.stream()
        .filter(response -> response.getId().equals(waiting1.getWaitingId()))
        .findFirst().get();
    assertThat(waiting1Result.getOrder().getOrderLineItems()).hasSize(2)
        .extracting("menuId", "name")
        .contains(
            tuple("menuId-1-1", "메뉴1-1"),
            tuple("menuId-1-2", "메뉴1-2")
        );

    // 웨이팅2는 메뉴가 1개
    RemoteListWaitingResponse waiting2Result = results.stream()
        .filter(response -> response.getId().equals(waiting2.getWaitingId()))
        .findFirst().get();
    assertThat(waiting2Result.getOrder().getOrderLineItems()).hasSize(1)
        .extracting("menuId", "name")
        .contains(
            tuple("menuId-2-1", "메뉴2-1")
        );

    // 주문이 없는 웨이팅(웨이팅3)은 아래와 같이 반환한다.
    RemoteListWaitingResponse waitingWithoutOrder = results.stream()
        .filter(response -> response.getId().equals(waiting3.getWaitingId()))
        .findFirst().get();
    assertEquals("", waitingWithoutOrder.getOrder().getId());
    assertEquals(BigDecimal.valueOf(0), waitingWithoutOrder.getOrder().getTotalPrice());
    assertEquals(0, waitingWithoutOrder.getOrder().getOrderLineItems().size());
  }

  @DisplayName("원격 웨이팅 등록 전 중복/3회초과 여부 검증 성공")
  @Test
  void checkWaitingBeforeRegister() {
    String channelShopId = "5";
    String shopId = "shopId";
    String phoneNumber = "010-0000-0000";
    LocalDate operationDate = LocalDate.of(2023, 3, 8);

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, channelShopId);

    RemoteWaitingCheckBeforeRegisterServiceRequest request =
        RemoteWaitingCheckBeforeRegisterServiceRequest.builder()
            .customerPhone(phoneNumber)
            .build();

    customerRepository.save(new CustomerEntity(PhoneNumberUtils.ofKr(phoneNumber)));
    remoteWaitingApiService.checkWaitingBeforeRegister(channelShopIdMapping, operationDate,
        request);
  }

  @DisplayName("원격 웨이팅은 같은 매장에 중복으로 등록할 수 없다.")
  @Test
  void duplicate_shop_register_checkWaitingBeforeRegister() {
    String channelShopId = "5";
    String shopId = "shopId";
    String phoneNumber = "010-0000-0000";
    LocalDate operationDate = LocalDate.of(2023, 3, 8);

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, channelShopId);

    RemoteWaitingCheckBeforeRegisterServiceRequest request =
        RemoteWaitingCheckBeforeRegisterServiceRequest.builder()
            .customerPhone(phoneNumber)
            .build();

    CustomerEntity savedCustomerEntity = customerRepository.save(
        new CustomerEntity(PhoneNumberUtils.ofKr(phoneNumber)));

    WaitingEntity build = createWaitingEntity(shopId, operationDate, savedCustomerEntity.getSeq());
    waitingRepository.save(build);

    assertThatThrownBy(
        () -> remoteWaitingApiService.checkWaitingBeforeRegister(channelShopIdMapping,
            operationDate, request)
    ).hasMessageEndingWith("이미 웨이팅 등록된 매장입니다.");
  }

  @DisplayName("원격 웨이팅은 같은 번호로 동시에 3개를 초과할 수 없다.")
  @Test
  void no_more_three_times_register_checkWaitingBeforeRegister() {
    String channelShopId = "5";
    String shopId = "shopId";
    String phoneNumber = "010-0000-0000";
    LocalDate operationDate = LocalDate.of(2023, 3, 8);

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, channelShopId);

    RemoteWaitingCheckBeforeRegisterServiceRequest request =
        RemoteWaitingCheckBeforeRegisterServiceRequest.builder()
            .customerPhone(phoneNumber)
            .build();

    CustomerEntity savedCustomerEntity = customerRepository.save(
        new CustomerEntity(PhoneNumberUtils.ofKr(phoneNumber)));

    Long customerSeq = savedCustomerEntity.getSeq();
    WaitingEntity waiting1 = createWaitingEntity("shopId1", operationDate, customerSeq);
    WaitingEntity waiting2 = createWaitingEntity("shopId2", operationDate, customerSeq);
    WaitingEntity waiting3 = createWaitingEntity("shopId3", operationDate, customerSeq);
    waitingRepository.save(waiting1);
    waitingRepository.save(waiting2);
    waitingRepository.save(waiting3);

    assertThatThrownBy(
        () -> remoteWaitingApiService.checkWaitingBeforeRegister(
            channelShopIdMapping,
            operationDate,
            request
        )
    ).hasMessageEndingWith("웨이팅은 동시에 3회까지만 등록할 수 있습니다.");
  }

  private WaitingEntity createWaitingEntity(
      String shopId,
      LocalDate operationDate,
      Long customerSeq
  ) {
    return WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID())
        .registerChannel(RegisterChannel.CATCH_APP)
        .waitingStatus(WAITING)
        .waitingDetailStatus(WaitingDetailStatus.WAITING)
        .customerSeq(customerSeq)
        .operationDate(operationDate)
        .waitingNumbers(WaitingNumber.builder()
            .waitingOrder(1)
            .waitingNumber(101).build())
        .build();
  }

  private WaitingEntity createWaiting(String shopId, LocalDate operationDate, int waitingOrder,
      String seatOptionName, WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .registerChannel(WAITING_APP)
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
        .personOptionsData(PersonOptionsData.builder()
            .personOptions(List.of())
            .build()
        )
        .totalPersonCount(1)
        .build();
    return waitingRepository.save(waiting);
  }

  private ChannelMappingEntity createChannelMapping(String channelShopId, String shopId) {
    ChannelMappingEntity entity = ChannelMappingEntity.builder()
        .channelId(ServiceChannelId.CATCHTABLE_B2C.getValue())
        .channelShopId(channelShopId)
        .shopId(shopId)
        .build();
    return jpaChannelMappingRepository.save(entity);
  }

  private ShopEntity createShop(String shopId) {
    ShopEntity shop = ShopEntity.builder()
        .shopId(shopId)
        .shopName("shopName")
        .shopAddress("shopAddress")
        .shopTelNumber("shopTelNumber")
        .build();
    return shopRepository.save(shop);
  }

  private HomeSettingsEntity createHomeSettings(String shopId, String modeType) {
    SeatOptions defaultModeOptions = SeatOptions.builder()
        .name("착석")
        .isUsedExpectedWaitingPeriod(false)
        .build();
    List<SeatOptions> tableModeOptions = List.of(
        SeatOptions.builder()
            .name("홀")
            .isUsedExpectedWaitingPeriod(false)
            .build(),
        SeatOptions.builder()
            .name("바")
            .isUsedExpectedWaitingPeriod(false)
            .build()
    );

    HomeSettingsEntity homeSettings = HomeSettingsEntity.builder()
        .shopId(shopId)
        .homeSettingsData(HomeSettingsData.builder()
            .waitingModeType(modeType)
            .defaultModeSettings(defaultModeOptions)
            .tableModeSettings(tableModeOptions)
            .build()
        )
        .build();
    return homeSettingsRepository.save(homeSettings);
  }

  private OrderEntity createOrder(String shopId, String waitingId, LocalDate operationDate,
      OrderType orderType, BigDecimal totalPrice) {
    OrderEntity orderEntity = OrderEntity.builder()
        .shopId(shopId)
        .waitingId(waitingId)
        .orderId(UUIDUtil.shortUUID())
        .operationDate(operationDate)
        .orderType(orderType)
        .totalPrice(Price.of(totalPrice))
        .build();

    return orderRepository.save(orderEntity);
  }

  private OrderLineItemEntity createOrderLineItem(String orderId, String menuId, String menuName,
      BigDecimal unitPrice, BigDecimal linePrice,
      int quantity) {
    OrderLineItemEntity orderLineItemEntity = OrderLineItemEntity.builder()
        .orderId(orderId)
        .menuId(menuId)
        .menuName(menuName)
        .unitPrice(Price.of(unitPrice))
        .linePrice(Price.of(linePrice))
        .quantity(quantity)
        .build();

    return orderLineItemRepository.save(orderLineItemEntity);
  }

}