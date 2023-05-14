package co.wadcorp.waiting.api.service.waiting;

import static co.wadcorp.waiting.data.domain.order.OrderType.SHOP;
import static co.wadcorp.waiting.data.domain.waiting.WaitingStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemRepository;
import co.wadcorp.waiting.data.domain.order.OrderRepository;
import co.wadcorp.waiting.data.domain.waiting.PersonOptionsData;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.support.Price;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingUndoValidateApiServiceTest extends IntegrationTest {

  @Autowired
  private WaitingUndoValidateApiService waitingUndoValidateApiService;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderLineItemRepository orderLineItemRepository;

  @DisplayName("고객이 되돌리기 요청 시 주문했던 메뉴가 삭제된 경우 되돌리기가 불가하다.")
  @Test
  void cannotUndoIfMenuWasDeleted() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 4, 3);

    WaitingEntity waiting = createWaiting(shopId, operationDate, WAITING,
        WaitingDetailStatus.WAITING, 1, "홀");

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3, 3000);

    menu1.delete();
    menuRepository.save(menu1);

    String orderId = UUIDUtil.shortUUID();
    createOrder(orderId, shopId, waiting.getWaitingId(), operationDate, List.of(
        createOrderLineItem(orderId, menu1, 1),
        createOrderLineItem(orderId, menu2, 1),
        createOrderLineItem(orderId, menu3, 1)
    ));

    // when // then
    AppException appException = assertThrows(
        AppException.class,
        () -> waitingUndoValidateApiService.validateOrder(waiting.getWaitingId(), operationDate)
    );

    assertThat(appException.getDisplayMessage())
        .isEqualTo(ErrorCode.NOT_FOUND_ORDER_LINE_ITEM_MENU.getMessage());
  }

  private WaitingEntity createWaiting(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus, int waitingOrder,
      String seatOptionName
  ) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
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
        .personOptionsData(PersonOptionsData.builder().build())
        .build();
    return waitingRepository.save(waiting);
  }

  private MenuEntity createMenu(String shopId, String name, int ordering, long unitPrice) {
    MenuEntity menu = MenuEntity.builder()
        .menuId(UUIDUtil.shortUUID())
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .unitPrice(Price.of(unitPrice))
        .build();
    return menuRepository.save(menu);
  }

  private OrderEntity createOrder(String orderId, String shopId, String waitingId,
      LocalDate operationDate, List<OrderLineItemEntity> orderLineItems) {
    OrderEntity order = OrderEntity.builder()
        .orderId(orderId)
        .shopId(shopId)
        .waitingId(waitingId)
        .operationDate(operationDate)
        .orderType(SHOP)
        .totalPrice(orderLineItems.stream()
            .map(OrderLineItemEntity::getLinePrice)
            .reduce(Price.ZERO, Price::add)
        )
        .build();

    order.settingOrderLineItems(orderLineItems);
    return orderRepository.save(order);
  }

  private OrderLineItemEntity createOrderLineItem(String orderId, MenuEntity menu, int quantity) {
    Price unitPrice = menu.getUnitPrice();

    OrderLineItemEntity orderLineItem = OrderLineItemEntity.builder()
        .orderId(orderId)
        .menuId(menu.getMenuId())
        .menuName(menu.getName())
        .unitPrice(unitPrice)
        .linePrice(unitPrice.times(quantity))
        .quantity(quantity)
        .build();

    return orderLineItemRepository.save(orderLineItem);
  }

}