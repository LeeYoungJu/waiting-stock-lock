package co.wadcorp.waiting.api.controller.waiting.management.dto.request;

import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateWaitingOrderServiceRequest;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateWaitingOrderRequest {

  private OrderDto order;

  public UpdateWaitingOrderRequest() {
  }

  @Builder
  private UpdateWaitingOrderRequest(OrderDto order) {
    this.order = order;
  }

  public UpdateWaitingOrderServiceRequest toServiceRequest() {

    return UpdateWaitingOrderServiceRequest.builder()
        .order(UpdateWaitingOrderServiceRequest.OrderDto.builder()
            .orderLineItems(order.orderLineItems.stream()
                .map(menu -> UpdateWaitingOrderServiceRequest.OrderLineItemDto.builder()
                    .menuId(menu.menuId)
                    .name(menu.name)
                    .unitPrice(menu.unitPrice)
                    .quantity(menu.quantity)
                    .build())
                .toList())
            .build())
        .build();
  }

  @Getter
  public static class OrderDto {

    private List<OrderLineItemDto> orderLineItems;

    public OrderDto() {
    }

    @Builder
    private OrderDto(List<OrderLineItemDto> orderLineItems) {
      this.orderLineItems = orderLineItems;
    }
  }

  @Getter
  public static class OrderLineItemDto {

    private String menuId;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;

    public OrderLineItemDto() {
    }

    @Builder
    private OrderLineItemDto(String menuId, String name, BigDecimal unitPrice, int quantity) {
      this.menuId = menuId;
      this.name = name;
      this.unitPrice = unitPrice;
      this.quantity = quantity;
    }
  }
}
