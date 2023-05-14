package co.wadcorp.waiting.data.domain.order.history;

import java.util.List;

public interface OrderLineItemHistoryRepository {

  <S extends OrderLineItemHistoryEntity> List<S> saveAll(Iterable<S> orderLineItemHistoryEntities);
}
