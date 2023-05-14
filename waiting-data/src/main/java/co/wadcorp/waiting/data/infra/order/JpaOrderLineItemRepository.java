package co.wadcorp.waiting.data.infra.order;

import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderLineItemRepository extends OrderLineItemRepository,
    JpaRepository<OrderLineItemEntity, Long> {

  <S extends OrderLineItemEntity> List<S> saveAll(Iterable<S> orderLineItemEntities);
}
