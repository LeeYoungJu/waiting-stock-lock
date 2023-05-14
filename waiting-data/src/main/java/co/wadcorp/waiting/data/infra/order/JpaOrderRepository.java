package co.wadcorp.waiting.data.infra.order;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderRepository extends OrderRepository, JpaRepository<OrderEntity, Long> {

}
