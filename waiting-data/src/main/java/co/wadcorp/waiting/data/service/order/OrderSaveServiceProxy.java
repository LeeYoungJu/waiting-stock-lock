package co.wadcorp.waiting.data.service.order;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Primary
@Service
@RequiredArgsConstructor
public class OrderSaveServiceProxy implements OrderSaveService {

    private final RedissonClient redissonClient;

    private final OrderSaveServiceImplement orderSaveService;

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        RLock lock = redissonClient.getLock("order-menu-sales-quantity-increase-");

        try {
            boolean isAchieve = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if(!isAchieve) {
                throw new RuntimeException("can not achieve lock");
            }

            return orderSaveService.save(orderEntity);
        } catch (InterruptedException e) {
            throw new AppException();
        } finally {
            lock.unlock();
        }
    }
}
