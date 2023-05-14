package co.wadcorp.waiting.api.internal.service.waiting.dto.request;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemStatus;
import co.wadcorp.waiting.data.domain.order.OrderStatus;
import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteOrderDto {

  public static final RemoteOrderDto EMPTY = new RemoteOrderDto(BigDecimal.valueOf(0), List.of());

  private BigDecimal totalPrice;
  private List<OrderLineItem> orderLineItems;

  @Builder
  private RemoteOrderDto(BigDecimal totalPrice, List<OrderLineItem> orderLineItems) {
    this.totalPrice = totalPrice;
    this.orderLineItems = orderLineItems;
  }

  public boolean haveNoMenu() {
    return Objects.isNull(orderLineItems) || orderLineItems.size() == 0;
  }

  public boolean haveMenus() {
    return !haveNoMenu();
  }

  public List<String> getMenuIds() {
    return this.orderLineItems.stream()
        .map(OrderLineItem::getMenuId)
        .toList();
  }

  public List<MenuQuantity> toMenuQuantity() {
    return orderLineItems.stream()
        .map(item -> MenuQuantity.builder()
            .menuId(item.menuId)
            .name(item.name)
            .quantity(item.quantity)
            .build())
        .toList();
  }

  public boolean isLineItemsPriceValid() {
    return orderLineItems.stream()
        .allMatch(item ->
            item.linePrice
                .equals(item.unitPrice.multiply(BigDecimal.valueOf(item.quantity))));
  }

  public boolean isTotalPriceValid() {
    return totalPrice.equals(orderLineItems.stream()
        .map(OrderLineItem::getLinePrice)
        .reduce(BigDecimal.valueOf(0), BigDecimal::add));
  }

  public OrderEntity toEntity(String shopId, String waitingId, LocalDate operationDate,
      OrderType orderType, RemoteOrderDto orderDto) {
    String orderId = UUIDUtil.shortUUID();

    return OrderEntity.builder()
        .orderId(orderId)
        .shopId(shopId)
        .waitingId(waitingId)
        .operationDate(operationDate)
        .orderType(orderType)
        .totalPrice(Price.of(orderDto.getTotalPrice()))
        .orderLineItems(orderDto.getOrderLineItems().stream()
            .map(orderLineItem -> orderLineItem.toEntity(orderId))
            .toList()
        )
        .build();
  }

  @Getter
  public static class OrderLineItem {
    private String menuId;
    private String name;
    private BigDecimal unitPrice;
    private BigDecimal linePrice;
    private int quantity;

    public OrderLineItem() {
    }

    @Builder
    private OrderLineItem(String menuId, String name, BigDecimal unitPrice, BigDecimal linePrice, int quantity) {
      this.menuId = menuId;
      this.name = name;
      this.unitPrice = unitPrice;
      this.linePrice = linePrice;
      this.quantity = quantity;
    }

    private OrderLineItemEntity toEntity(String orderId) {
      return OrderLineItemEntity.builder()
          .orderId(orderId)
          .menuId(menuId)
          .menuName(name)
          .unitPrice(Price.of(unitPrice))
          .linePrice(Price.of(linePrice))
          .quantity(quantity)
          .build();
    }
  }
}
