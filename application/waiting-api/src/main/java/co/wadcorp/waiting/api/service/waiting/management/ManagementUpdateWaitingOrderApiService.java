package co.wadcorp.waiting.api.service.waiting.management;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.libs.stream.StreamUtils.groupingByList;

import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateWaitingOrderServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingOrderResponse;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingOrderResponse.OrderDto;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingOrderResponse.OrderDto.OrderLineItemDto;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.order.OrderLineItemEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.event.ChangedMenuStockEvent;
import co.wadcorp.waiting.data.query.displaymenu.DisplayCategoryQueryRepository;
import co.wadcorp.waiting.data.query.displaymenu.DisplayMenuQueryRepository;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import co.wadcorp.waiting.data.query.menu.MenuQueryRepository;
import co.wadcorp.waiting.data.query.stock.StockQueryRepository;
import co.wadcorp.waiting.data.service.order.OrderService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManagementUpdateWaitingOrderApiService {

  private final OrderService orderService;

  private final DisplayCategoryQueryRepository displayCategoryQueryRepository;
  private final DisplayMenuQueryRepository displayMenuQueryRepository;
  private final MenuQueryRepository menuQueryRepository;
  private final StockQueryRepository stockQueryRepository;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public ManagementWaitingOrderResponse updateOrder(String shopId, String orderId,
      UpdateWaitingOrderServiceRequest request, String deviceId
  ) {
    OrderEntity orderEntity = orderService.findByOrderId(orderId);
    List<OrderLineItemEntity> requestOrderLineItems = request.toOrderLineItems(orderId);

    // 주문 변경 및 저장
    OrderEntity savedOrder = orderService.update(orderEntity, requestOrderLineItems);
    eventPublisher.publishEvent(new ChangedMenuStockEvent(shopId, deviceId));

    // 뭐가 더 좋을까..
    DisplayMappingType displayMappingType = DisplayMappingType.valueOf(
        savedOrder.getOrderType().name());

    List<DisplayMenuEntity> displayMenuEntities = displayMenuQueryRepository.findAllByShopId(shopId,
        displayMappingType);

    List<String> categoryIds = displayMenuEntities.stream()
        .filter(DisplayMenuEntity::getIsChecked)
        .map(DisplayMenuEntity::getCategoryId)
        .toList();
    List<DisplayCategoryDto> displayCategoryDtos = displayCategoryQueryRepository.findDisplayCategoriesByCategoryIds(
        categoryIds, displayMappingType
    );

    Map<String, List<DisplayMenuEntity>> categoryDisplayMenu = groupingByList(displayMenuEntities,
        DisplayMenuEntity::getCategoryId);

    Map<String, StockEntity> menuStock = getMenuStock(savedOrder.getOperationDate(),
        displayMenuEntities);

    Map<String, StockEntity> orderMenuStock = getOrderLineItemMenuStock(savedOrder.getOperationDate(), savedOrder);
    Map<String, MenuEntity> orderMenuIdMenuMap = getOrderLineItemMenu(savedOrder);

    return ManagementWaitingOrderResponse.of(
        displayCategoryDtos.stream()
            .sorted()
            .map(displayCategory ->
                ManagementWaitingOrderResponse.OrderCategory.of(
                    displayCategory, categoryDisplayMenu, menuStock
                )
            ).toList(),

        OrderDto.builder()
            .orderLineItems(
                savedOrder.getOrderLineItems()
                    .stream()
                    .filter(OrderLineItemEntity::isNotCanceledItem)
                    .map(item -> {
                      StockEntity stockEntity = orderMenuStock.get(item.getMenuId());
                      MenuEntity menuEntity = orderMenuIdMenuMap.get(item.getMenuId());

                      return OrderLineItemDto.builder()
                          .menuId(item.getMenuId())
                          .name(item.getMenuName())
                          .quantity(item.getQuantity())
                          .unitPrice(item.getUnitPrice().value())
                          .linePrice(item.getLinePrice().value())
                          .isUsedDailyStock(stockEntity.isUsedDailyStock())
                          .remainingQuantity(stockEntity.getRemainingQuantity())
                          .isDeletedMenu(menuEntity.getIsDeleted())
                          .build();
                    })
                    .toList())
            .build()
    );
  }

  private Map<String, StockEntity> getMenuStock(LocalDate operationDate,
      List<DisplayMenuEntity> displayMenuEntities
  ) {
    return stockQueryRepository.findAllBy(displayMenuEntities.stream()
            .map(DisplayMenuEntity::getMenuId)
            .toList(), operationDate)
        .stream()
        .collect(Collectors.toMap(StockEntity::getMenuId, item -> item, (item1, item2) -> item1));
  }

  private Map<String, MenuEntity> getOrderLineItemMenu(OrderEntity orderEntity) {
    return menuQueryRepository.findAllByIds(
            convert(orderEntity.getOrderLineItems(), OrderLineItemEntity::getMenuId)
        )
        .stream()
        .collect(Collectors.toMap(MenuEntity::getMenuId, item -> item, (item1, item2) -> item1));
  }

  private Map<String, StockEntity> getOrderLineItemMenuStock(LocalDate operationDate,
      OrderEntity orderEntity
  ) {
    return stockQueryRepository.findAllBy(
            convert(orderEntity.getOrderLineItems(), OrderLineItemEntity::getMenuId),
            operationDate
        )
        .stream()
        .collect(Collectors.toMap(StockEntity::getMenuId, item -> item, (item1, item2) -> item1));
  }

}
