package co.wadcorp.waiting.api.internal.service.waiting;

import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest.OrderLineItem;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingOrderValidateRequest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingOrderValidateServiceRequest;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteWaitingOrderValidateApiServiceTest extends IntegrationTest {

  @Autowired
  private RemoteWaitingOrderValidateApiService remoteWaitingOrderValidateApiService;

  @Autowired
  private OrderSettingsRepository orderSettingsRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private StockRepository stockRepository;

  @DisplayName("선주문 정보가 문제 없으면 검증 시 예외가 발생하지 않는다.")
  @Test
  void validateOrderMenusWithNormal() {
    // given
    String shopId = "SHOP_ID";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "111");
    LocalDate operationDate = LocalDate.of(2023, 5, 8);
    boolean isPossibleOrder = true;
    saveOrderSettings(shopId, isPossibleOrder);
    saveMenu(shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50);
    saveMenu(shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50);
    saveStock("MENU_ID_1", operationDate, 100);
    saveStock("MENU_ID_2", operationDate, 100);

    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(16000))
        .orderLineItems(List.of(
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build(),
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("메뉴2")
                    .unitPrice(BigDecimal.valueOf(3000))
                    .linePrice(BigDecimal.valueOf(6000))
                    .quantity(2)
                    .build()
            )
        )
        .build();

    RemoteWaitingOrderValidateServiceRequest request = createRequestWithOrderDto(orderDto);

    // when // then (예외발생 X)
    remoteWaitingOrderValidateApiService.validateOrderMenus(
        channelShopIdMapping,
        operationDate,
        request);
  }

  @DisplayName("isPossibleOrder가 off 인 매장은 검증 시 예외가 발생한다.")
  @Test
  void validateOrderMenusWithPossibleOrderOff() {
    // given
    String shopId = "SHOP_ID";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "111");
    LocalDate operationDate = LocalDate.of(2023, 5, 8);
    boolean isPossibleOrder = false;  // <== 예외 원인
    saveOrderSettings(shopId, isPossibleOrder);
    RemoteWaitingOrderValidateServiceRequest request = createDefaultRequest();

    // when // then
    AppException appException = assertThrows(AppException.class, () ->
        remoteWaitingOrderValidateApiService.validateOrderMenus(
            channelShopIdMapping,
            operationDate,
            request));
    assertEquals(ErrorCode.NOT_POSSIBLE_ORDER.getMessage(), appException.getMessage());
  }

  @DisplayName("잔여 재고량보다 큰 수량을 주문하면 예외가 발생한다.")
  @Test
  void validateOrderMenusWithOverStockOrder() {
    // given
    String shopId = "SHOP_ID";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "111");
    LocalDate operationDate = LocalDate.of(2023, 5, 8);
    boolean isPossibleOrder = true;
    saveOrderSettings(shopId, isPossibleOrder);
    saveMenu(shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50);
    saveMenu(shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50);
    saveStock("MENU_ID_1", operationDate, 100);
    saveStock("MENU_ID_2", operationDate, 1); // <== 예외 원인

    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(16000))
        .orderLineItems(List.of(
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build(),
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("메뉴2")
                    .unitPrice(BigDecimal.valueOf(3000))
                    .linePrice(BigDecimal.valueOf(6000))
                    .quantity(2)  // 위에서 보면 메뉴2의 잔여수량은 1개인데 2개를 주문하는 상황
                    .build()
            )
        )
        .build();

    RemoteWaitingOrderValidateServiceRequest request = createRequestWithOrderDto(orderDto);

    // when // then
    AppException appException = assertThrows(AppException.class, () ->
        remoteWaitingOrderValidateApiService.validateOrderMenus(
            channelShopIdMapping,
            operationDate,
            request));
    assertEquals(ErrorCode.OUT_OF_STOCK.getMessage(), appException.getMessage());
  }

  @DisplayName("팀 당 주문 가능 수량을 초과하면 예외가 발생한다.")
  @Test
  void validateOrderMenusWithExceedOrderQuantityPerTeam() {
    // given
    String shopId = "SHOP_ID";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "111");
    LocalDate operationDate = LocalDate.of(2023, 5, 8);
    boolean isPossibleOrder = true;
    saveOrderSettings(shopId, isPossibleOrder);
    MenuEntity menu1 = saveMenuWithExceedingPerTeamInfo(
        shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50, 2); // <== 예외 원인
    MenuEntity menu2 = saveMenuWithExceedingPerTeamInfo(
        shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50, 5);
    saveStock("MENU_ID_1", operationDate, 100);
    saveStock("MENU_ID_2", operationDate, 100);

    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(36000))
        .orderLineItems(List.of(
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(30000))
                    .quantity(3)  // 위에서 보면 팀 당 주문 수량이 2개인데 3개를 주문하는 상황
                    .build(),
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("메뉴2")
                    .unitPrice(BigDecimal.valueOf(3000))
                    .linePrice(BigDecimal.valueOf(6000))
                    .quantity(2)
                    .build()
            )
        )
        .build();

    RemoteWaitingOrderValidateServiceRequest request = createRequestWithOrderDto(orderDto);

    // when // then
    AppException appException = assertThrows(AppException.class, () ->
        remoteWaitingOrderValidateApiService.validateOrderMenus(
            channelShopIdMapping,
            operationDate,
            request));
    assertEquals(ErrorCode.EXCEEDING_ORDER_QUANTITY_PER_TEAM.getMessage(), appException.getMessage());
  }

  @DisplayName("주문 메뉴 금액이 잘못되면 예외가 발생한다.")
  @Test
  void validateOrderMenusWithInvalidPrice() {
    // given
    String shopId = "SHOP_ID";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "111");
    LocalDate operationDate = LocalDate.of(2023, 5, 8);
    boolean isPossibleOrder = true;
    saveOrderSettings(shopId, isPossibleOrder);
    MenuEntity menu1 = saveMenuWithExceedingPerTeamInfo(
        shopId, "MENU_ID_1", "메뉴1", 1, 10000, 50, 5);
    MenuEntity menu2 = saveMenuWithExceedingPerTeamInfo(
        shopId, "MENU_ID_2", "메뉴2", 2, 3000, 50, 5);
    saveStock("MENU_ID_1", operationDate, 100);
    saveStock("MENU_ID_2", operationDate, 100);

    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(35000))  // <== 금액 합계 잘못됨
        .orderLineItems(List.of(
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(30000))
                    .quantity(3)
                    .build(),
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_2")
                    .name("메뉴2")
                    .unitPrice(BigDecimal.valueOf(3000))
                    .linePrice(BigDecimal.valueOf(6000))
                    .quantity(2)
                    .build()
            )
        )
        .build();

    RemoteWaitingOrderValidateServiceRequest request = createRequestWithOrderDto(orderDto);

    // when // then
    AppException appException = assertThrows(AppException.class, () ->
        remoteWaitingOrderValidateApiService.validateOrderMenus(
            channelShopIdMapping,
            operationDate,
            request));
    assertEquals(ErrorCode.INVALID_TOTAL_PRICE.getMessage(), appException.getMessage());
  }

  private OrderSettingsEntity saveOrderSettings(String shopId, boolean isPossibleOrder) {
    return orderSettingsRepository.save(OrderSettingsEntity.builder()
        .shopId(shopId)
        .orderSettingsData(OrderSettingsData.of(
            isPossibleOrder
        ))
        .build());
  }

  private MenuEntity saveMenu(String shopId, String menuId, String name, int ordering,
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

  private MenuEntity saveMenuWithExceedingPerTeamInfo(String shopId, String menuId, String name, int ordering,
      int unitPrice, int dailyStock, int menuQuantityPerTeam) {
    return menuRepository.save(MenuEntity.builder()
        .shopId(shopId)
        .menuId(menuId)
        .name(name)
        .ordering(ordering)
        .unitPrice(Price.of(unitPrice))
        .isUsedDailyStock(true)
        .dailyStock(dailyStock)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(menuQuantityPerTeam)
        .build()
    );
  }

  private StockEntity saveStock(String menuId, LocalDate operationDate, int stock) {
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

  private RemoteWaitingOrderValidateServiceRequest createDefaultRequest() {
    return RemoteWaitingOrderValidateServiceRequest.builder()
        .order(RemoteOrderDto.builder()
            .totalPrice(BigDecimal.valueOf(16000))
            .orderLineItems(List.of(
                RemoteOrderDto.OrderLineItem.builder()
                    .menuId("MENU_ID_1")
                    .name("메뉴1")
                    .unitPrice(BigDecimal.valueOf(10000))
                    .linePrice(BigDecimal.valueOf(10000))
                    .quantity(1)
                    .build(),

                RemoteOrderDto.OrderLineItem.builder()
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
  }

  private RemoteWaitingOrderValidateServiceRequest createRequestWithOrderDto(
      RemoteOrderDto orderDto) {
    return RemoteWaitingOrderValidateServiceRequest.builder()
        .order(orderDto)
        .build();
  }

}