package co.wadcorp.waiting.data.query.order;

import static co.wadcorp.waiting.data.domain.order.QOrderEntity.orderEntity;
import static co.wadcorp.waiting.data.domain.order.QOrderLineItemEntity.orderLineItemEntity;
import static com.querydsl.core.group.GroupBy.groupBy;

import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class WaitingOrderQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Optional<WaitingOrderDto> findByWaitingId(String waitingIds) {
    return queryFactory.from(orderEntity)
        .join(orderLineItemEntity).on(orderLineItemEntity.orderId.eq(orderEntity.orderId))
        .where(orderEntity.waitingId.in(waitingIds))
        .transform(
            groupBy(orderEntity.orderId).list(
                Projections.fields(
                    WaitingOrderDto.class,
                    orderEntity.orderId,
                    orderEntity.waitingId,
                    orderEntity.orderType,
                    orderEntity.orderStatus,
                    orderEntity.totalPrice,
                    GroupBy.list(
                        Projections.fields(
                            WaitingOrderDto.OrderLineItem.class,
                            orderLineItemEntity.orderId,
                            orderLineItemEntity.menuId,
                            orderLineItemEntity.menuName,
                            orderLineItemEntity.orderLineItemStatus,
                            orderLineItemEntity.unitPrice,
                            orderLineItemEntity.linePrice,
                            orderLineItemEntity.quantity
                        )
                    ).as("orderLineItems")
                )))
        .stream().findFirst();
  }

  public List<WaitingOrderDto> findByWaitingIds(List<String> waitingIds) {
    return queryFactory.from(orderEntity)
        .join(orderLineItemEntity).on(orderLineItemEntity.orderId.eq(orderEntity.orderId))
        .where(orderEntity.waitingId.in(waitingIds))
        .transform(
            groupBy(orderEntity.orderId).list(
                Projections.fields(
                    WaitingOrderDto.class,
                    orderEntity.orderId,
                    orderEntity.waitingId,
                    orderEntity.orderType,
                    orderEntity.orderStatus,
                    orderEntity.totalPrice,
                    GroupBy.list(
                        Projections.fields(
                            WaitingOrderDto.OrderLineItem.class,
                            orderLineItemEntity.orderId,
                            orderLineItemEntity.menuId,
                            orderLineItemEntity.menuName,
                            orderLineItemEntity.orderLineItemStatus,
                            orderLineItemEntity.unitPrice,
                            orderLineItemEntity.linePrice,
                            orderLineItemEntity.quantity
                        )
                    ).as("orderLineItems")
                )));
  }

  public WaitingOrderDto findByOrderId(String orderId) {
    return queryFactory.from(orderEntity)
        .join(orderLineItemEntity).on(orderLineItemEntity.orderId.eq(orderEntity.orderId))
        .where(orderEntity.orderId.eq(orderId))
        .transform(
            groupBy(orderEntity.orderId).list(
                Projections.fields(
                    WaitingOrderDto.class,
                    orderEntity.orderId,
                    orderEntity.waitingId,
                    orderEntity.orderType,
                    orderEntity.orderStatus,
                    orderEntity.totalPrice,
                    GroupBy.list(
                        Projections.fields(
                            WaitingOrderDto.OrderLineItem.class,
                            orderLineItemEntity.orderId,
                            orderLineItemEntity.menuId,
                            orderLineItemEntity.menuName,
                            orderLineItemEntity.orderLineItemStatus,
                            orderLineItemEntity.unitPrice,
                            orderLineItemEntity.linePrice,
                            orderLineItemEntity.quantity
                        )
                    ).as("orderLineItems")
                )))
        .stream()
        .findFirst()
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "주문 정보를 찾을 수 없습니다."));

  }
}
