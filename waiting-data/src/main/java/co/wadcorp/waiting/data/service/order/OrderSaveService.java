package co.wadcorp.waiting.data.service.order;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import org.springframework.stereotype.Service;

public interface OrderSaveService {

    OrderEntity save(OrderEntity orderEntity);
}
