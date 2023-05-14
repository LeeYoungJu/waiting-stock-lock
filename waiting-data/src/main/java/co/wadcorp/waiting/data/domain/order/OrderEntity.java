package co.wadcorp.waiting.data.domain.order;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.data.domain.order.history.OrderHistoryEntity;
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
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cw_order",
    indexes = {
        @Index(name = "cw_order_order_id_index", columnList = "order_id"),
        @Index(name = "cw_order_shop_id_index", columnList = "shop_id"),
        @Index(name = "cw_order_waiting_id_index", columnList = "waiting_id")
    }
)
@Entity
public class OrderEntity extends BaseEntity {

  public static final OrderEntity EMPTY_ORDER = new OrderEntity();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

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

  @Transient
  private List<OrderLineItemEntity> orderLineItems;

  @Builder
  private OrderEntity(String orderId, String shopId, String waitingId, LocalDate operationDate,
      OrderType orderType, Price totalPrice, List<OrderLineItemEntity> orderLineItems) {
    this.orderId = orderId;
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.operationDate = operationDate;
    this.orderType = orderType;
    this.orderStatus = OrderStatus.CREATED;
    this.totalPrice = totalPrice;
    this.orderLineItems = orderLineItems;
  }

  public OrderHistoryEntity toHistoryEntity() {
    return OrderHistoryEntity.builder()
        .orderSeq(seq)
        .orderId(orderId)
        .shopId(shopId)
        .waitingId(waitingId)
        .operationDate(operationDate)
        .orderType(orderType)
        .orderStatus(orderStatus)
        .totalPrice(totalPrice)
        .build();
  }

  public void settingOrderLineItems(List<OrderLineItemEntity> orderLineItemEntities) {
    this.orderLineItems = orderLineItemEntities;
  }

  public void update(Price totalPrice, List<OrderLineItemEntity> orderLineItemEntities) {
    Map<String, OrderLineItemEntity> orderLineItemEntityMap = orderLineItemEntities.stream()
        .collect(Collectors.toMap(OrderLineItemEntity::getMenuId, item -> item));

    this.totalPrice = totalPrice;

    deleted(orderLineItemEntityMap);
    updated(orderLineItemEntityMap);
    created(orderLineItemEntities);
  }

  public List<String> getMenuIds() {
    return convert(orderLineItems, OrderLineItemEntity::getMenuId);
  }

  private void updated(Map<String, OrderLineItemEntity> orderLineItemEntityMap) {
    this.orderLineItems.stream()
        .filter(item -> Objects.nonNull(orderLineItemEntityMap.get(item.getMenuId())))
        .forEach(item -> {
          OrderLineItemEntity orderLineItemEntity = orderLineItemEntityMap.get(item.getMenuId());
          item.update(orderLineItemEntity);
        });
  }

  private void deleted(Map<String, OrderLineItemEntity> orderLineItemEntityMap) {
    this.orderLineItems.stream()
        .filter(item -> Objects.isNull(orderLineItemEntityMap.get(item.getMenuId())))
        .forEach(OrderLineItemEntity::canceled);
  }

  private void created(List<OrderLineItemEntity> orderLineItemEntities) {
    Map<String, OrderLineItemEntity> orderLineItemEntityMap = this.orderLineItems.stream()
        .collect(Collectors.toMap(OrderLineItemEntity::getMenuId, item -> item));

    this.orderLineItems.addAll(orderLineItemEntities.stream()
        .filter(item -> Objects.isNull(orderLineItemEntityMap.get(item.getMenuId())))
        .toList());
  }

  public boolean isCanceled() {
    return this.orderStatus == OrderStatus.CANCEL;
  }

  public void cancel() {
    this.orderStatus = OrderStatus.CANCEL;
    this.orderLineItems.forEach(OrderLineItemEntity::canceled);
  }

  public void undo() {
    this.orderStatus = OrderStatus.CREATED;
    this.orderLineItems.forEach(OrderLineItemEntity::undo);

  }
}
