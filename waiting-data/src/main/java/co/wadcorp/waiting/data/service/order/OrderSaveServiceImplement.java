package co.wadcorp.waiting.data.service.order;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemRepository;
import co.wadcorp.waiting.data.domain.order.OrderRepository;
import co.wadcorp.waiting.data.domain.order.history.OrderHistoryRepository;
import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryEntity;
import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static co.wadcorp.libs.stream.StreamUtils.convert;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderSaveServiceImplement implements OrderSaveService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final StockRepository stockRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final OrderLineItemHistoryRepository orderLineItemHistoryRepository;

    public OrderEntity save(OrderEntity orderEntity) {

        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        orderHistoryRepository.save(savedOrderEntity.toHistoryEntity());

        saveOrderLineItem(orderEntity);

        // 판매수량 증가
        List<OrderLineItemEntity> orderLineItems = orderEntity.getOrderLineItems();
        orderLineItems.forEach(
                item -> {
                    StockEntity stock = stockRepository.findByMenuIdAndOperationDate(item.getMenuId(), orderEntity.getOperationDate())
                            .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_STOCK));
                    stock.increaseSalesQuantity(item.getQuantity());
                    stockRepository.save(stock);
//          stockRepository.increaseSalesQuantity(
//                  item.getMenuId(), orderEntity.getOperationDate(), item.getQuantity()
//          );
                }
        );

        return savedOrderEntity;
    }

    private void saveOrderLineItem(OrderEntity orderEntity) {
        List<OrderLineItemEntity> orderLineItemEntities = orderLineItemRepository.saveAll(
                orderEntity.getOrderLineItems());

        List<OrderLineItemHistoryEntity> orderLineItemHistoryEntities = convert(orderLineItemEntities,
                OrderLineItemEntity::toHistoryEntity);

        orderLineItemHistoryRepository.saveAll(orderLineItemHistoryEntities);
    }
}
