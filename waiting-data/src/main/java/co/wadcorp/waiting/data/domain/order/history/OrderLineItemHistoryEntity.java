package co.wadcorp.waiting.data.domain.order.history;

import co.wadcorp.waiting.data.domain.order.OrderLineItemStatus;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.Price;
import co.wadcorp.waiting.data.support.PriceConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
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
@Table(name = "cw_order_line_item_history",
    indexes = {
        @Index(name = "cw_order_line_item_history_order_line_item_seq_index", columnList = "order_line_item_seq"),
        @Index(name = "cw_order_line_item_history_order_id_index", columnList = "order_id"),
        @Index(name = "cw_order_line_item_history_menu_id_index", columnList = "menu_id")
    }
)
@Entity
public class OrderLineItemHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "order_line_item_seq")
  private Long orderLineItemSeq;

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
  private OrderLineItemHistoryEntity(Long orderLineItemSeq, String orderId, String menuId,
      String menuName, OrderLineItemStatus orderLineItemStatus, Price unitPrice, Price linePrice,
      int quantity) {
    this.orderLineItemSeq = orderLineItemSeq;
    this.orderId = orderId;
    this.menuId = menuId;
    this.menuName = menuName;
    this.orderLineItemStatus = orderLineItemStatus;
    this.unitPrice = unitPrice;
    this.linePrice = linePrice;
    this.quantity = quantity;
  }

}
