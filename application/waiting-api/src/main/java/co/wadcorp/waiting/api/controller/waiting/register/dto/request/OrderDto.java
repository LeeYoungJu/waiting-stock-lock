package co.wadcorp.waiting.api.controller.waiting.register.dto.request;

import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemStatus;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.support.Price;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

  public List<OrderLineItemEntity> toOrderLineItemEntity(String orderId) {
    return this.orderLineItems.stream()
        .map(menu -> OrderLineItemEntity.builder()
            .orderId(orderId)
            .menuId(menu.menuId)
            .menuName(menu.name)
            .unitPrice(Price.of(menu.unitPrice))
            .linePrice(Price.of(menu.linePrice))
            .quantity(menu.quantity)
            .build())
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

  @JsonIgnore
  public List<String> getMenuIds() {
    return this.orderLineItems.stream()
        .map(OrderLineItemDto::getMenuId)
        .toList();
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
  }




}
