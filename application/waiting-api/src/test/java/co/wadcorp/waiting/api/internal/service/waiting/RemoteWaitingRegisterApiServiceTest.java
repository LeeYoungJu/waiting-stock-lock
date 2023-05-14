package co.wadcorp.waiting.api.internal.service.waiting;

import static co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto.OrderLineItem;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingRegisterServiceRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingRegisterServiceRequest.PersonOptionVO;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.CreatedOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingRegisterResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.shop.ShopRepository;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.domain.shop.operation.pause.RemoteAutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteWaitingRegisterApiServiceTest extends IntegrationTest {

  @Autowired
  private RemoteWaitingRegisterApiService remoteWaitingRegisterApiService;
  @Autowired
  private ShopOperationInfoRepository shopOperationInfoRepository;
  @Autowired
  private OrderSettingsRepository orderSettingsRepository;
  @Autowired
  private MenuRepository menuRepository;
  @Autowired
  private StockRepository stockRepository;
  @Autowired
  private OptionSettingsRepository optionSettingsRepository;
  @Autowired
  private HomeSettingsRepository homeSettingsRepository;
  @Autowired
  private ShopRepository shopRepository;


  @DisplayName("원격 웨이팅 등록 시 원격 영업시간을 체크한다.")
  @Test
  void checkOpenWithRemoteOperationDateTime() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 4, 20);

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(operationDate, LocalTime.of(10, 30))); // 현장은 ON, 원격은 OFF된 시간

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "1");

    createShopOperationInfo(shopId, operationDate, OPEN,
        LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(22, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(11, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(21, 0))
    );

    RemoteWaitingRegisterServiceRequest request = RemoteWaitingRegisterServiceRequest.builder()
        .build();

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> remoteWaitingRegisterApiService.register(channelShopIdMapping, operationDate,
            nowDateTime, request)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.NOT_OPEN_WAITING_OPERATION.getMessage());
  }

  @DisplayName("원격 웨이팅 등록 시 원격 일시정지 시간을 체크한다.")
  @Test
  void checkOpenWithRemoteAutoPauseDateTime() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 4, 20);

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(operationDate, LocalTime.of(14, 30))); // 현장은 ON, 원격은 OFF된 시간

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "1");

    createShopOperationInfo(shopId, operationDate, OPEN,
        LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(22, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(11, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(21, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(14, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(15, 0))
    );

    RemoteWaitingRegisterServiceRequest request = RemoteWaitingRegisterServiceRequest.builder()
        .build();

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> remoteWaitingRegisterApiService.register(channelShopIdMapping, operationDate,
            nowDateTime, request)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.NOT_OPEN_WAITING_OPERATION.getMessage());
  }

  @DisplayName("원격 웨이팅 등록 시 주문정보도 저장할 수 있다.")
  @Test
  void registerWaitingWithOrder() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 5, 11);
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "1");

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(operationDate, LocalTime.of(15, 30)));

    createShopOperationInfo(shopId, operationDate, OPEN,
        LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(22, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(11, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(21, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(14, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(15, 0))
    );

    createShop(shopId);
    createOrderSettings(shopId, true);
    createOptionSettings(shopId, "PERSON_OPTION_ID", "성인");
    createHomeSettings(shopId, "SHOP_SEAT_OPTION_ID", "TAKE_OUT_SEAT_OPTION_ID");

    createMenu(shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50);
    createMenu(shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50);
    createStock("MENU_ID_1", operationDate, 100);
    createStock("MENU_ID_2", operationDate, 100);

    RemoteWaitingRegisterServiceRequest request = RemoteWaitingRegisterServiceRequest.builder()
        .tableId("SHOP_SEAT_OPTION_ID")
        .totalPersonCount(2)
        .personOptions(List.of(PersonOptionVO.builder()
            .id("PERSON_OPTION_ID")
            .count(1)
            .additionalOptions(List.of())
            .build())
        )
        .phoneNumber("010-1111-2222")
        .order(RemoteOrderDto.builder()
            .totalPrice(BigDecimal.valueOf(16000))
            .orderLineItems(List.of(
                OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build(),
                OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("메뉴2")
                    .unitPrice(BigDecimal.valueOf(3000))
                    .linePrice(BigDecimal.valueOf(6000))
                    .quantity(2)
                    .build()
            ))
            .build()
        )
        .build();

    when(waitingNumberService.getWaitingNumber(any(), any())).thenReturn(WaitingNumber.ofDefault());

    // when
    RemoteWaitingRegisterResponse response = remoteWaitingRegisterApiService.register(
        channelShopIdMapping, operationDate,
        nowDateTime, request);

    // then
    CreatedOrderDto createdOrderDto = response.getOrder();
    assertNotNull(createdOrderDto);
    assertEquals(BigDecimal.valueOf(16000), createdOrderDto.getTotalPrice());
    List<CreatedOrderDto.OrderLineItem> orderLineItems = createdOrderDto.getOrderLineItems();
    Assertions.assertThat(orderLineItems).hasSize(2)
        .extracting("name")
        .contains("메뉴1", "메뉴2");

  }

  @DisplayName("원격 웨이팅 등록 시 주문정보가 없어도 등록이 가능하다.")
  @Test
  void registerWaitingWithoutOrder() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 5, 11);
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "1");

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(operationDate, LocalTime.of(15, 30)));

    createShopOperationInfo(shopId, operationDate, OPEN,
        LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(22, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(11, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(21, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(14, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(15, 0))
    );

    createShop(shopId);
    createOrderSettings(shopId, true);
    createOptionSettings(shopId, "PERSON_OPTION_ID", "성인");
    createHomeSettings(shopId, "SHOP_SEAT_OPTION_ID", "TAKE_OUT_SEAT_OPTION_ID");

    createMenu(shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50);
    createMenu(shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50);
    createStock("MENU_ID_1", operationDate, 100);
    createStock("MENU_ID_2", operationDate, 100);

    RemoteWaitingRegisterServiceRequest request = RemoteWaitingRegisterServiceRequest.builder()
        .tableId("SHOP_SEAT_OPTION_ID")
        .totalPersonCount(2)
        .personOptions(List.of(PersonOptionVO.builder()
            .id("PERSON_OPTION_ID")
            .count(1)
            .additionalOptions(List.of())
            .build())
        )
        .phoneNumber("010-1111-2222")
        .build();

    when(waitingNumberService.getWaitingNumber(any(), any())).thenReturn(WaitingNumber.ofDefault());

    // when
    RemoteWaitingRegisterResponse response = remoteWaitingRegisterApiService.register(
        channelShopIdMapping, operationDate,
        nowDateTime, request);

    // then
    CreatedOrderDto createdOrderDto = response.getOrder();
    assertNotNull(createdOrderDto);
    assertEquals(BigDecimal.valueOf(0), createdOrderDto.getTotalPrice());
    List<CreatedOrderDto.OrderLineItem> orderLineItems = createdOrderDto.getOrderLineItems();
    assertEquals(0, orderLineItems.size());
  }

  @DisplayName("원격 웨이팅 등록 시 주문정보가 있으면 재고를 검증한다.")
  @Test
  void checkOrderDataWhenRegisterWaiting() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 5, 11);
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "1");

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(operationDate, LocalTime.of(15, 30)));

    createShopOperationInfo(shopId, operationDate, OPEN,
        LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(22, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(11, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(21, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(14, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(15, 0))
    );

    createShop(shopId);
    createOrderSettings(shopId, true);
    createOptionSettings(shopId, "PERSON_OPTION_ID", "성인");
    createHomeSettings(shopId, "SHOP_SEAT_OPTION_ID", "TAKE_OUT_SEAT_OPTION_ID");

    createMenu(shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50);
    createMenu(shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50);
    createStock("MENU_ID_1", operationDate, 100);
    createStock("MENU_ID_2", operationDate, 1);   // 재고를 한개로 설정

    RemoteWaitingRegisterServiceRequest request = RemoteWaitingRegisterServiceRequest.builder()
        .tableId("SHOP_SEAT_OPTION_ID")
        .totalPersonCount(2)
        .personOptions(List.of(PersonOptionVO.builder()
            .id("PERSON_OPTION_ID")
            .count(1)
            .additionalOptions(List.of())
            .build())
        )
        .phoneNumber("010-1111-2222")
        .order(RemoteOrderDto.builder()
            .totalPrice(BigDecimal.valueOf(16000))
            .orderLineItems(List.of(
                OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build(),
                OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("메뉴2")
                    .unitPrice(BigDecimal.valueOf(3000))
                    .linePrice(BigDecimal.valueOf(6000))
                    .quantity(2)  // 재고가 한개지만 두개를 요청 => OUT_OF_STOCK 예상
                    .build()
            ))
            .build()
        )
        .build();

    when(waitingNumberService.getWaitingNumber(any(), any())).thenReturn(WaitingNumber.ofDefault());

    // then
    AppException appException = assertThrows(AppException.class, () -> {
      RemoteWaitingRegisterResponse response = remoteWaitingRegisterApiService.register(
          channelShopIdMapping, operationDate,
          nowDateTime, request);
    });
    assertEquals(ErrorCode.OUT_OF_STOCK.getMessage(), appException.getMessage());
  }

  @DisplayName("원격 웨이팅 등록 시 주문정보가 있으면 주문설정을 검증한다.")
  @Test
  void checkOrderSettingWhenRegisterWaiting() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 5, 11);
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "1");

    ZonedDateTime nowDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(operationDate, LocalTime.of(15, 30)));

    createShopOperationInfo(shopId, operationDate, OPEN,
        LocalDateTime.of(operationDate, LocalTime.of(10, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(22, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(11, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(21, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(14, 0)),
        LocalDateTime.of(operationDate, LocalTime.of(15, 0))
    );

    createShop(shopId);
    createOrderSettings(shopId, false);   // <== 선주문 설정 off
    createOptionSettings(shopId, "PERSON_OPTION_ID", "성인");
    createHomeSettings(shopId, "SHOP_SEAT_OPTION_ID", "TAKE_OUT_SEAT_OPTION_ID");

    createMenu(shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50);
    createMenu(shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50);
    createStock("MENU_ID_1", operationDate, 100);
    createStock("MENU_ID_2", operationDate, 100);

    RemoteWaitingRegisterServiceRequest request = RemoteWaitingRegisterServiceRequest.builder()
        .tableId("SHOP_SEAT_OPTION_ID")
        .totalPersonCount(2)
        .personOptions(List.of(PersonOptionVO.builder()
            .id("PERSON_OPTION_ID")
            .count(1)
            .additionalOptions(List.of())
            .build())
        )
        .phoneNumber("010-1111-2222")
        .order(RemoteOrderDto.builder()
            .totalPrice(BigDecimal.valueOf(16000))
            .orderLineItems(List.of(
                OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build(),
                OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("메뉴2")
                    .unitPrice(BigDecimal.valueOf(3000))
                    .linePrice(BigDecimal.valueOf(6000))
                    .quantity(2)
                    .build()
            ))
            .build()
        )
        .build();

    when(waitingNumberService.getWaitingNumber(any(), any())).thenReturn(WaitingNumber.ofDefault());

    // then
    AppException appException = assertThrows(AppException.class, () -> {
      RemoteWaitingRegisterResponse response = remoteWaitingRegisterApiService.register(
          channelShopIdMapping, operationDate,
          nowDateTime, request);
    });
    assertEquals(ErrorCode.NOT_POSSIBLE_ORDER.getMessage(), appException.getMessage());
  }

  private ShopOperationInfoEntity createShopOperationInfo(String shopId, LocalDate operationDate,
      RegistrableStatus registrableStatus, LocalDateTime operationStartDateTime,
      LocalDateTime operationEndDateTime, LocalDateTime remoteOperationStartDateTime,
      LocalDateTime remoteOperationEndDateTime) {
    ShopOperationInfoEntity shopOperationInfo = ShopOperationInfoEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .registrableStatus(registrableStatus)
        .operationStartDateTime(ZonedDateTimeUtils.ofSeoul(operationStartDateTime))
        .operationEndDateTime(ZonedDateTimeUtils.ofSeoul(operationEndDateTime))
        .remoteOperationStartDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationStartDateTime))
        .remoteOperationEndDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationEndDateTime))
        .build();

    return shopOperationInfoRepository.save(shopOperationInfo);
  }

  private ShopOperationInfoEntity createShopOperationInfo(String shopId, LocalDate operationDate,
      RegistrableStatus registrableStatus, LocalDateTime operationStartDateTime,
      LocalDateTime operationEndDateTime, LocalDateTime remoteOperationStartDateTime,
      LocalDateTime remoteOperationEndDateTime, LocalDateTime remoteAutoPauseStartDateTime,
      LocalDateTime remoteAutoPauseEndDateTime) {
    ShopOperationInfoEntity shopOperationInfo = ShopOperationInfoEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .registrableStatus(registrableStatus)
        .operationStartDateTime(ZonedDateTimeUtils.ofSeoul(operationStartDateTime))
        .operationEndDateTime(ZonedDateTimeUtils.ofSeoul(operationEndDateTime))
        .remoteOperationStartDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationStartDateTime))
        .remoteOperationEndDateTime(ZonedDateTimeUtils.ofSeoul(remoteOperationEndDateTime))
        .remoteAutoPauseInfo(RemoteAutoPauseInfo.builder()
            .remoteAutoPauseStartDateTime(ZonedDateTimeUtils.ofSeoul(remoteAutoPauseStartDateTime))
            .remoteAutoPauseEndDateTime(ZonedDateTimeUtils.ofSeoul(remoteAutoPauseEndDateTime))
            .build()
        )
        .build();

    return shopOperationInfoRepository.save(shopOperationInfo);
  }

  private void createShop(String shopId) {
    ShopEntity shopEntity = ShopEntity.builder()
        .shopId(shopId)
        .shopName("TEST_SHOP")
        .shopAddress("SHOP_ADDRESS")
        .shopTelNumber("010-0000-0000")
        .isUsedRemoteWaiting(true)
        .isTest(false)
        .isMembership(true)
        .build();

    shopRepository.save(shopEntity);
  }

  private void createOrderSettings(String shopId, boolean isPossibleOrder) {
    OrderSettingsEntity orderSettingsEntity = OrderSettingsEntity.builder()
        .shopId(shopId)
        .orderSettingsData(OrderSettingsData.builder()
            .isPossibleOrder(isPossibleOrder)
            .build()
        )
        .build();

    orderSettingsRepository.save(orderSettingsEntity);
  }

  private void createOptionSettings(String shopId, String personOptionSettingsId, String name) {
    OptionSettingsEntity optionSettingsEntity = new OptionSettingsEntity(shopId,
        OptionSettingsData.of(true, List.of(
            PersonOptionSetting.builder()
                .id(personOptionSettingsId)
                .name(name)
                .isDisplayed(true)
                .isSeat(true)
                .isDefault(true)
                .canModify(true)
                .additionalOptions(List.of())
                .build()
        )));

    optionSettingsRepository.save(optionSettingsEntity);
  }

  private void createHomeSettings(String shopId, String shopSeatOptionId,
      String takeoutSeatOptionId) {
    HomeSettingsEntity homeSettingsEntity = HomeSettingsEntity.builder()
        .shopId(shopId)
        .homeSettingsData(HomeSettingsData.builder()
            .waitingModeType("DEFAULT")
            .defaultModeSettings(SeatOptions.builder()
                .id("DEFAULT_MODE_SETTINGS_ID")
                .name("포장")
                .minSeatCount(1)
                .maxSeatCount(7)
                .expectedWaitingPeriod(5)
                .isUsedExpectedWaitingPeriod(false)
                .isDefault(true)
                .isTakeOut(true)
                .build()
            )
            .tableModeSettings(List.of(
                SeatOptions.builder()
                    .id(shopSeatOptionId)
                    .name("홀 2인석")
                    .minSeatCount(1)
                    .maxSeatCount(2)
                    .expectedWaitingPeriod(5)
                    .isUsedExpectedWaitingPeriod(true)
                    .isDefault(true)
                    .isTakeOut(false)
                    .build(),
                SeatOptions.builder()
                    .id(takeoutSeatOptionId)
                    .name("포장2")
                    .minSeatCount(3)
                    .maxSeatCount(4)
                    .expectedWaitingPeriod(15)
                    .isUsedExpectedWaitingPeriod(true)
                    .isDefault(true)
                    .isTakeOut(true)
                    .build()
            ))
            .build()
        )
        .build();

    homeSettingsRepository.save(homeSettingsEntity);
  }

  private MenuEntity createMenu(String shopId, String menuId, String name, int ordering,
      int unitPrice, int dailyStock) {
    return menuRepository.save(MenuEntity.builder()
        .shopId(shopId)
        .menuId(menuId)
        .name(name)
        .ordering(ordering)
        .unitPrice(Price.of(unitPrice))
        .isUsedDailyStock(true)
        .dailyStock(dailyStock)
        .build()
    );
  }

  private StockEntity createStock(String menuId, LocalDate operationDate, int stock) {
    return stockRepository.save(StockEntity.builder()
        .menuId(menuId)
        .operationDate(operationDate)
        .isUsedDailyStock(true)
        .stock(stock)
        .salesQuantity(0)
        .isOutOfStock(false)
        .build()
    );
  }

}