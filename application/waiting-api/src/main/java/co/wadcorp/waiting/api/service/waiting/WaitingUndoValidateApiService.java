package co.wadcorp.waiting.api.service.waiting;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.service.menu.MenuService;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.stock.StockService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class WaitingUndoValidateApiService {

  private final OrderService orderService;
  private final MenuService menuService;
  private final StockService stockService;

  @Transactional
  public void validateOrder(String waitingId, LocalDate operationDate) {
    OrderEntity orderEntity = orderService.getByWaitingId(waitingId);
    if (orderEntity == OrderEntity.EMPTY_ORDER) {
      return;
    }

    List<OrderLineItemEntity> orderLineItems = orderEntity.getOrderLineItems();
    List<String> menuIds = orderEntity.getMenuIds();

    Map<String, MenuEntity> menuIdMenuEntityMap = menuService.getMenuIdMenuEntityMap(menuIds);
    boolean notMatchItems = checkNotMatchItems(orderLineItems, menuIdMenuEntityMap);
    if (notMatchItems) {
      throw AppException.ofBadRequest(ErrorCode.NOT_FOUND_ORDER_LINE_ITEM_MENU);
    }

    Map<String, StockEntity> menuIdMenuStockMap = stockService.getMenuIdMenuStockMap(
        operationDate, menuIds);
    boolean isOutOfStock = checkOutOfStock(orderLineItems, menuIdMenuStockMap);
    if (isOutOfStock) {
      throw AppException.ofBadRequest(ErrorCode.OUT_OF_STOCK);
    }
  }

  private boolean checkNotMatchItems(List<OrderLineItemEntity> orderLineItems,
      Map<String, MenuEntity> menuIdMenuEntityMap) {
    return orderLineItems.stream()
        .anyMatch(item -> {
          MenuEntity menuEntity = menuIdMenuEntityMap.get(item.getMenuId());
          return Objects.isNull(menuEntity) || menuEntity.getIsDeleted();
        });
  }

  private boolean checkOutOfStock(List<OrderLineItemEntity> orderLineItems,
      Map<String, StockEntity> menuIdMenuStockMap) {
    return orderLineItems.stream()
        .anyMatch(item -> {
          StockEntity stock = menuIdMenuStockMap.get(item.getMenuId());
          return Objects.isNull(stock) || (stock.isUsedDailyStock() && stock.checkOutOfStock(
              item.getQuantity()));
        });
  }

}
