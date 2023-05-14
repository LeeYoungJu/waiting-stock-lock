package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 원격 웨이팅 목록 조회 시 웨이팅 정보에 추가되는 주문정보 클래스
 */
@Getter
public class RemoteWaitingListOrderMenuDto {

  public static final RemoteWaitingListOrderMenuDto EMPTY = new RemoteWaitingListOrderMenuDto(
      "", BigDecimal.valueOf(0), List.of()
  );

  private final String id;
  private final BigDecimal totalPrice;
  private final List<OrderLineItem> orderLineItems;

  @Builder
  private RemoteWaitingListOrderMenuDto(String id, BigDecimal totalPrice,
      List<OrderLineItem> orderLineItems) {
    this.id = id;
    this.totalPrice = totalPrice;
    this.orderLineItems = orderLineItems;
  }

  public static RemoteWaitingListOrderMenuDto of(WaitingOrderDto orderDto) {
    return RemoteWaitingListOrderMenuDto.builder()
        .id(orderDto.getOrderId())
        .totalPrice(orderDto.getTotalPrice().value())
        .orderLineItems(orderDto.getOrderLineItems().stream()
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

    private static OrderLineItem of(WaitingOrderDto.OrderLineItem item) {
      return OrderLineItem.builder()
          .menuId(item.getMenuId())
          .name(item.getMenuName())
          .unitPrice(item.getUnitPrice().value())
          .linePrice(item.getLinePrice().value())
          .quantity(item.getQuantity())
          .build();
    }
  }
}
