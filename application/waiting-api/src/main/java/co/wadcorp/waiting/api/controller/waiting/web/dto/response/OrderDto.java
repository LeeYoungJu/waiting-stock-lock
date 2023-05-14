package co.wadcorp.waiting.api.controller.waiting.web.dto.response;

import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto.OrderLineItem;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderDto {

  private BigDecimal totalPrice;
  private List<OrderLineItemDto> orderLineItems;

  public OrderDto() {
  }

  @Builder
  private OrderDto(BigDecimal totalPrice, List<OrderLineItemDto> orderLineItems) {
    this.totalPrice = totalPrice;
    this.orderLineItems = orderLineItems;
  }

  @Getter
  public static class OrderLineItemDto {

    private String menuId;
    private String name;
    private BigDecimal unitPrice;
    private BigDecimal linePrice;
    private int quantity;

    public OrderLineItemDto() {
    }

    @Builder
    private OrderLineItemDto(String menuId, String name, BigDecimal unitPrice, BigDecimal linePrice, int quantity) {
      this.menuId = menuId;
      this.name = name;
      this.unitPrice = unitPrice;
      this.linePrice = linePrice;
      this.quantity = quantity;
    }

    public static OrderLineItemDto of(OrderLineItem item) {
      return OrderLineItemDto.builder()
          .menuId(item.getMenuId())
          .name(item.getMenuName())
          .quantity(item.getQuantity())
          .unitPrice(item.getUnitPrice().value())
          .linePrice(item.getLinePrice().value())
          .build();
    }
  }
}
