package co.wadcorp.waiting.data.domain.order.history;

import co.wadcorp.waiting.data.domain.order.OrderStatus;
import co.wadcorp.waiting.data.domain.order.OrderType;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cw_order_history",
    indexes = {
        @Index(name = "cw_order_history_order_seq_index", columnList = "order_seq"),
        @Index(name = "cw_order_history_order_id_index", columnList = "order_id"),
        @Index(name = "cw_order_history_shop_id_index", columnList = "shop_id"),
        @Index(name = "cw_order_history_waiting_id_index", columnList = "waiting_id")
    }
)
@Entity
public class OrderHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "order_seq")
  private Long orderSeq;

  @Column(name = "order_id")
  private String orderId;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "waiting_id")
  private String waitingId;

  @Column(name = "operation_date")
  private LocalDate operationDate;

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  private OrderType orderType;

  @Column(name = "order_status")
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @Column(name = "total_price")
  @Convert(converter = PriceConverter.class)
  private Price totalPrice;

  @Builder
  private OrderHistoryEntity(Long orderSeq, String orderId, String shopId, String waitingId,
      LocalDate operationDate, OrderType orderType, OrderStatus orderStatus, Price totalPrice) {
    this.orderSeq = orderSeq;
    this.orderId = orderId;
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.operationDate = operationDate;
    this.orderType = orderType;
    this.orderStatus = orderStatus;
    this.totalPrice = totalPrice;
  }

}
