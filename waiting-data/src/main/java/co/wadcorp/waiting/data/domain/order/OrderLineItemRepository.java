package co.wadcorp.waiting.data.domain.order;

import java.util.List;

public interface OrderLineItemRepository {

  OrderLineItemEntity save(OrderLineItemEntity orderLineItemEntity);

  <S extends OrderLineItemEntity> List<S> saveAll(Iterable<S> orderLineItemEntities);

  List<OrderLineItemEntity> findAllByOrderId(String orderId);
}
