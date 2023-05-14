package co.wadcorp.waiting.api.service.waiting.management.dto.request;

import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemStatus;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateWaitingOrderServiceRequest {

  private final OrderDto order;

  public BigDecimal calculateTotalPrice() {
    return this.order.orderLineItems.stream()
        .map(OrderLineItemDto::calculateLintPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Builder
  private UpdateWaitingOrderServiceRequest(OrderDto order) {
    this.order = order;
  }

  public List<OrderLineItemEntity> toOrderLineItems(String orderId) {
    return this.order.orderLineItems
        .stream()
        .map(item ->OrderLineItemEntity.builder()
            .orderId(orderId)
            .menuId(item.menuId)
            .menuName(item.name)
            .unitPrice(Price.of(item.unitPrice))
            .linePrice(Price.of(item.calculateLintPrice()))
            .quantity(item.quantity)
            .build())
        .toList();
  }

  @Getter
  public static class OrderDto {
    private final List<OrderLineItemDto> orderLineItems;

    @Builder
    private OrderDto(List<OrderLineItemDto> orderLineItems) {
      this.orderLineItems = orderLineItems;
    }
  }

  @Getter
  public static class OrderLineItemDto {
    private final String menuId;
    private final String name;
    private final BigDecimal unitPrice;
    private final int quantity;

    @Builder
    private OrderLineItemDto(String menuId, String name, BigDecimal unitPrice, int quantity) {
      this.menuId = menuId;
      this.name = name;
      this.unitPrice = unitPrice;
      this.quantity = quantity;
    }

    public BigDecimal calculateLintPrice() {
      return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
  }
}
