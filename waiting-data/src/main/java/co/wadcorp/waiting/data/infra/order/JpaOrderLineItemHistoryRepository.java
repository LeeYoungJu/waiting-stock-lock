package co.wadcorp.waiting.data.infra.order;

import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryEntity;
import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderLineItemHistoryRepository extends OrderLineItemHistoryRepository,
    JpaRepository<OrderLineItemHistoryEntity, Long> {

  <S extends OrderLineItemHistoryEntity> List<S> saveAll(Iterable<S> orderLineItemHistoryEntities);
}
