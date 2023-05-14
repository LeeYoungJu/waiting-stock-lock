package co.wadcorp.waiting.api.service.waiting.management.dto.response;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ManagementWaitingOrderResponse {

  private final List<OrderCategory> categories;
  private final OrderDto order;

  @Builder
  private ManagementWaitingOrderResponse(List<OrderCategory> categories, OrderDto order) {
    this.categories = categories;
    this.order = order;
  }

  public static ManagementWaitingOrderResponse of(List<OrderCategory> categories, OrderDto order) {
    return ManagementWaitingOrderResponse.builder()
        .categories(categories)
        .order(order)
        .build();
  }


  @Getter
  public static class OrderCategory {

    private final String id;
    private final String name;
    private final int ordering;
    private final List<OrderMenu> menus;

    @Builder
    private OrderCategory(String id, String name, int ordering, List<OrderMenu> menus) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.menus = menus;
    }

    public static OrderCategory of(DisplayCategoryDto displayCategory,
        Map<String, List<DisplayMenuEntity>> categoryDisplayMenu,
        Map<String, StockEntity> menuStock) {
      return ManagementWaitingOrderResponse.OrderCategory.builder()
          .id(displayCategory.getCategoryId())
          .name(displayCategory.getCategoryName())
          .ordering(displayCategory.getOrdering())
          .menus(
              categoryDisplayMenu.getOrDefault(displayCategory.getCategoryId(), List.of())
                  .stream()
                  .sorted()
                  .filter(DisplayMenuEntity::getIsChecked)
                  .map(displayMenu -> {
                    StockEntity stockEntity = menuStock.get(displayMenu.getMenuId());
                    Integer remainingQuantity = stockEntity.getRemainingQuantity();

                    return OrderMenu.builder()
                        .id(displayMenu.getMenuId())
                        .name(displayMenu.getMenuName())
                        .ordering(displayMenu.getOrdering())
                        .unitPrice(displayMenu.getUnitPrice().value())
                        .isUsedDailyStock(stockEntity.isUsedDailyStock())
                        .remainingQuantity(remainingQuantity)
                        .build();
                  })
                  .toList()
          ).build();
    }


  }

  @Getter
  public static class OrderMenu {

    private final String id;
    private final String name;
    private final int ordering;
    private final BigDecimal unitPrice;
    private final Boolean isUsedDailyStock;
    private final Integer remainingQuantity;


    @Builder
    private OrderMenu(String id, String name, int ordering, BigDecimal unitPrice,
        boolean isUsedDailyStock, Integer remainingQuantity) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.unitPrice = unitPrice;
      this.isUsedDailyStock = isUsedDailyStock;
      this.remainingQuantity = remainingQuantity;
    }
  }

  @Getter
  public static class OrderDto {

    private final List<OrderLineItemDto> orderLineItems;

    @Builder
    private OrderDto(List<OrderLineItemDto> orderLineItems) {
      this.orderLineItems = orderLineItems;
    }

    @Getter
    public static class OrderLineItemDto {

      private final String menuId;
      private final String name;
      private final BigDecimal unitPrice;
      private final BigDecimal linePrice;
      private final int quantity;
      private final Boolean isUsedDailyStock;
      private final Integer remainingQuantity;
      private final Boolean isDeletedMenu;

      @Builder
      private OrderLineItemDto(String menuId, String name, BigDecimal unitPrice,
          BigDecimal linePrice, int quantity,
          Boolean isUsedDailyStock, Integer remainingQuantity, boolean isDeletedMenu) {
        this.menuId = menuId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.linePrice = linePrice;
        this.quantity = quantity;
        this.isUsedDailyStock = isUsedDailyStock;
        this.remainingQuantity = remainingQuantity;
        this.isDeletedMenu = isDeletedMenu;
      }
    }

  }
}
