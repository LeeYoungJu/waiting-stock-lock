package co.wadcorp.waiting.data.domain.order;

import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryEntity;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.data.support.PriceConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cw_order_line_item",
    indexes = {
        @Index(name = "cw_order_line_item_order_id_index", columnList = "order_id"),
        @Index(name = "cw_order_line_item_menu_id_index", columnList = "menu_id")
    }
)
@Entity
public class OrderLineItemEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "order_id")
  private String orderId;

  @Column(name = "menu_id")
  private String menuId;

  @Column(name = "menu_name")
  private String menuName;

  @Column(name = "order_line_item_status")
  @Enumerated(EnumType.STRING)
  private OrderLineItemStatus orderLineItemStatus;

  @Column(name = "unit_price")
  @Convert(converter = PriceConverter.class)
  private Price unitPrice;

  @Column(name = "line_price")
  @Convert(converter = PriceConverter.class)
  private Price linePrice;

  @Column(name = "quantity")
  private int quantity;

  @Builder
  private OrderLineItemEntity(String orderId, String menuId, String menuName,
      Price unitPrice, Price linePrice, int quantity) {
    this.orderId = orderId;
    this.menuId = menuId;
    this.menuName = menuName;
    this.orderLineItemStatus = OrderLineItemStatus.CREATED;
    this.unitPrice = unitPrice;
    this.linePrice = linePrice;
    this.quantity = quantity;
  }

  public OrderLineItemHistoryEntity toHistoryEntity() {
    return OrderLineItemHistoryEntity.builder()
        .orderLineItemSeq(seq)
        .orderId(orderId)
        .menuId(menuId)
        .menuName(menuName)
        .orderLineItemStatus(orderLineItemStatus)
        .unitPrice(unitPrice)
        .linePrice(linePrice)
        .quantity(quantity)
        .build();
  }

  public void update(OrderLineItemEntity orderLineItemEntity) {
    int updatedQuantity = orderLineItemEntity.getQuantity();
    if(updatedQuantity == quantity && this.orderLineItemStatus != OrderLineItemStatus.CANCELED) {
      return;
    }
    this.orderLineItemStatus = OrderLineItemStatus.CHANGED;
    this.quantity = updatedQuantity;
    this.unitPrice = orderLineItemEntity.getUnitPrice();
    this.linePrice = orderLineItemEntity.getLinePrice();
  }

  public void canceled() {
    this.orderLineItemStatus = OrderLineItemStatus.CANCELED;
  }

  public void undo() {
    this.orderLineItemStatus = OrderLineItemStatus.CREATED;
  }

  public boolean isCanceledItem() {
    return this.orderLineItemStatus == OrderLineItemStatus.CANCELED;
  }

  public boolean isNotCanceledItem() {
    return !isCanceledItem();
  }
}
