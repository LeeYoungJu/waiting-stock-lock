package co.wadcorp.waiting.data.query.order.dto;

import co.wadcorp.waiting.data.domain.order.OrderLineItemStatus;
import co.wadcorp.waiting.data.domain.order.OrderStatus;
import co.wadcorp.waiting.data.domain.order.OrderType;
import co.wadcorp.waiting.data.support.Price;
import java.util.List;
import lombok.Getter;

@Getter
public class WaitingOrderDto {

  public static WaitingOrderDto EMPTY_ORDER = new WaitingOrderDto();

  private String orderId;
  private String waitingId;
  private OrderType orderType;
  private OrderStatus orderStatus;
  private Price totalPrice;
  private List<OrderLineItem> orderLineItems;

  public WaitingOrderDto() {
  }

  @Getter
  public static class OrderLineItem {
    private String orderId;
    private String menuId;
    private String menuName;
    private OrderLineItemStatus orderLineItemStatus;
    private Price unitPrice;
    private Price linePrice;
    private int quantity;

    public OrderLineItem() {
    }

    public boolean isNotCanceledItem() {
      return this.orderLineItemStatus != OrderLineItemStatus.CANCELED;
    }
  }
}
