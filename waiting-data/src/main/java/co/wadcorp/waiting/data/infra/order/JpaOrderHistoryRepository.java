package co.wadcorp.waiting.data.infra.order;

import co.wadcorp.waiting.data.domain.order.history.OrderHistoryEntity;
import co.wadcorp.waiting.data.domain.order.history.OrderHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderHistoryRepository extends OrderHistoryRepository,
    JpaRepository<OrderHistoryEntity, Long> {

}
