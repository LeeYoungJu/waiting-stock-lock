package co.wadcorp.waiting.data.domain.order;

import java.util.Optional;

public interface OrderRepository {

  OrderEntity save(OrderEntity orderEntity);

  Optional<OrderEntity> findByOrderId(String orderId);

  Optional<OrderEntity> findByWaitingId(String waitingId);
}
