package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatedOrderDto {

  public static final CreatedOrderDto EMPTY = new CreatedOrderDto("", BigDecimal.valueOf(0),
      List.of());

  private final String id;
  private final BigDecimal totalPrice;
  private final List<OrderLineItem> orderLineItems;

  @Builder
  private CreatedOrderDto(String id, BigDecimal totalPrice,
      List<OrderLineItem> orderLineItems) {
    this.id = id;
    this.totalPrice = totalPrice;
    this.orderLineItems = orderLineItems;
  }

  public static CreatedOrderDto of(OrderEntity entity) {
    return CreatedOrderDto.builder()
        .id(entity.getOrderId())
        .totalPrice(entity.getTotalPrice().value())
        .orderLineItems(entity.getOrderLineItems().stream()
            .map(OrderLineItem::of)
            .toList()
        )
        .build();
  }

  @Getter
  public static class OrderLineItem {

    private final String menuId;
    private final String name;
    private final BigDecimal unitPrice;
    private final BigDecimal linePrice;
    private final int quantity;

    @Builder
    private OrderLineItem(String menuId, String name,
        BigDecimal unitPrice, BigDecimal linePrice, int quantity) {
      this.menuId = menuId;
      this.name = name;
      this.unitPrice = unitPrice;
      this.linePrice = linePrice;
      this.quantity = quantity;
    }

    private static OrderLineItem of(OrderLineItemEntity entity) {
      return OrderLineItem.builder()
          .menuId(entity.getMenuId())
          .name(entity.getMenuName())
          .unitPrice(entity.getUnitPrice().value())
          .linePrice(entity.getLinePrice().value())
          .quantity(entity.getQuantity())
          .build();
    }
  }
}
