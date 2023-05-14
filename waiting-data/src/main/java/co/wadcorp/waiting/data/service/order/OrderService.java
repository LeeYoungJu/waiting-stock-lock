package co.wadcorp.waiting.data.service.order;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.stream.StreamUtils;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemRepository;
import co.wadcorp.waiting.data.domain.order.OrderRepository;
import co.wadcorp.waiting.data.domain.order.history.OrderHistoryRepository;
import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryEntity;
import co.wadcorp.waiting.data.domain.order.history.OrderLineItemHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.support.Price;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderHistoryRepository orderHistoryRepository;
  private final OrderLineItemRepository orderLineItemRepository;
  private final OrderLineItemHistoryRepository orderLineItemHistoryRepository;

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;

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


  public void cancel(OrderEntity orderEntity) {
    if (orderEntity == OrderEntity.EMPTY_ORDER) {
      return;
    }

    orderEntity.cancel();
    List<OrderLineItemEntity> orderLineItems = orderEntity.getOrderLineItems();
    orderHistoryRepository.save(orderEntity.toHistoryEntity());

    saveOrderLineItem(orderEntity);

    orderLineItems.forEach(item -> {
      decreaseSalesQuantity(orderEntity.getOperationDate(), item.getMenuId(), item.getQuantity());
    });
  }

  public void undo(OrderEntity orderEntity) {
    if (orderEntity == OrderEntity.EMPTY_ORDER || !orderEntity.isCanceled()) {
      return;
    }

    orderEntity.undo();
    List<OrderLineItemEntity> orderLineItems = orderEntity.getOrderLineItems();
    orderHistoryRepository.save(orderEntity.toHistoryEntity());

    saveOrderLineItem(orderEntity);

    orderLineItems.forEach(item -> {
      increaseSalesQuantity(orderEntity.getOperationDate(), item.getMenuId(), item.getQuantity());
    });
  }

  public OrderEntity update(OrderEntity orderEntity,
      List<OrderLineItemEntity> requestOrderLineItems) {

    Map<String, OrderLineItemEntity> requestOrderLineItemsMap =
        StreamUtils.convertToMap(requestOrderLineItems, OrderLineItemEntity::getMenuId);

    List<OrderLineItemEntity> existingOrderLineItems = orderEntity.getOrderLineItems();
    Map<String, OrderLineItemEntity> existingOrderLineItemsMap =
        StreamUtils.convertToMap(existingOrderLineItems, OrderLineItemEntity::getMenuId);

    // 삭제된 메뉴 판매 수량 차감
    decreaseSalesQuantity(orderEntity.getOperationDate(), requestOrderLineItemsMap, existingOrderLineItems);

    // 변경 메뉴 판매 수량 변경
    updateSalesQuantity(orderEntity.getOperationDate(), requestOrderLineItemsMap, existingOrderLineItems);

    // 추가 메뉴 메뉴 판매 수량 증가
    increaseSalesQuantity(orderEntity.getOperationDate(), requestOrderLineItems, existingOrderLineItemsMap);

    Price totalPrice = requestOrderLineItems.stream()
        .map(OrderLineItemEntity::getLinePrice)
        .reduce(Price.ZERO, Price::add);

    orderEntity.update(totalPrice, requestOrderLineItems);
    saveOrderLineItem(orderEntity);

    return orderEntity;
  }

  public OrderEntity findByOrderId(String orderId) {
    OrderEntity orderEntity = orderRepository.findByOrderId(orderId)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "주문 정보를 찾을 수 없습니다."));

    List<OrderLineItemEntity> orderLineItemEntities = orderLineItemRepository.findAllByOrderId(
        orderId);
    orderEntity.settingOrderLineItems(orderLineItemEntities);

    return orderEntity;
  }

  public OrderEntity getByWaitingId(String waitingId) {
    OrderEntity orderEntity = orderRepository.findByWaitingId(waitingId)
        .orElse(OrderEntity.EMPTY_ORDER);

    if (orderEntity == OrderEntity.EMPTY_ORDER) {
      return orderEntity;
    }

    List<OrderLineItemEntity> orderLineItemEntities = orderLineItemRepository.findAllByOrderId(
        orderEntity.getOrderId());
    orderEntity.settingOrderLineItems(orderLineItemEntities);

    return orderEntity;
  }

  private void saveOrderLineItem(OrderEntity orderEntity) {
    List<OrderLineItemEntity> orderLineItemEntities = orderLineItemRepository.saveAll(
        orderEntity.getOrderLineItems());

    List<OrderLineItemHistoryEntity> orderLineItemHistoryEntities = convert(orderLineItemEntities,
        OrderLineItemEntity::toHistoryEntity);

    orderLineItemHistoryRepository.saveAll(orderLineItemHistoryEntities);
  }

  private void updateSalesQuantity(LocalDate operationDate,
      Map<String, OrderLineItemEntity> requestOrderLineItemsMap,
      List<OrderLineItemEntity> existingOrderLineItems
  ) {
    existingOrderLineItems.stream()
        .filter(item -> Objects.nonNull(requestOrderLineItemsMap.get(item.getMenuId())))
        .forEach(item -> {
          OrderLineItemEntity requestLineItem = requestOrderLineItemsMap.get(item.getMenuId());

          int quantity = item.isCanceledItem() ? 0 : item.getQuantity();
          int changedQuantity = requestLineItem.getQuantity() - quantity;
          if (changedQuantity > 0) {
            increaseSalesQuantity(operationDate, item.getMenuId(), changedQuantity);
            return;
          }
          decreaseSalesQuantity(operationDate, item.getMenuId(), item.getQuantity() - requestLineItem.getQuantity());
        });
  }

  private void increaseSalesQuantity(LocalDate operationDate,
      List<OrderLineItemEntity> requestOrderLineItems,
      Map<String, OrderLineItemEntity> existingOrderLineItemsMap
  ) {
    requestOrderLineItems.stream()
        .filter(item -> Objects.isNull(existingOrderLineItemsMap.get(item.getMenuId())))
        .forEach(item -> increaseSalesQuantity(operationDate, item.getMenuId(), item.getQuantity()));
  }

  private void increaseSalesQuantity(LocalDate operationDate, String menuId, int quantity) {
    stockRepository.findByMenuIdAndOperationDate(menuId, operationDate)
        .ifPresent(stock -> {
          stockRepository.increaseSalesQuantity(
              menuId, operationDate, quantity
          );
          saveIncreasedSalesQuantityHistory(stock, quantity);
        });
  }

  private void decreaseSalesQuantity(LocalDate operationDate,
      Map<String, OrderLineItemEntity> requestOrderLineItemsMap,
      List<OrderLineItemEntity> existingOrderLineItems
  ) {
    existingOrderLineItems.stream()
        .filter(item -> {
          OrderLineItemEntity orderLineItemEntity = requestOrderLineItemsMap.get(item.getMenuId());
          return Objects.isNull(orderLineItemEntity);
        })
        .forEach(item -> decreaseSalesQuantity(operationDate, item.getMenuId(), item.getQuantity()));
  }

  private void decreaseSalesQuantity(LocalDate operationDate, String menuId, int quantity) {
    stockRepository.findByMenuIdAndOperationDate(menuId, operationDate)
        .ifPresent(stock -> {
          stockRepository.decreaseSalesQuantity(
              menuId, operationDate, quantity
          );
          saveDecreasedQuantityHistory(stock, quantity);
        });
  }

  private void saveIncreasedSalesQuantityHistory(StockEntity stock, int quantity) {
    stockHistoryRepository.save(StockHistoryEntity.builder()
        .stockSeq(stock.getSeq())
        .menuId(stock.getMenuId())
        .operationDate(stock.getOperationDate())
        .isUsedDailyStock(stock.isUsedDailyStock())
        .stock(stock.getStock())
        .salesQuantity(stock.getSalesQuantity() + quantity)
        .isOutOfStock(stock.isOutOfStock())
        .build()
    );
  }

  private void saveDecreasedQuantityHistory(StockEntity stock, int quantity) {
    stockHistoryRepository.save(StockHistoryEntity.builder()
        .stockSeq(stock.getSeq())
        .menuId(stock.getMenuId())
        .operationDate(stock.getOperationDate())
        .isUsedDailyStock(stock.isUsedDailyStock())
        .stock(stock.getStock())
        .salesQuantity(stock.getSalesQuantity() - quantity)
        .isOutOfStock(stock.isOutOfStock())
        .build()
    );
  }


}
