package co.wadcorp.waiting.api.service.waiting.management.dto.response;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.exception.AppException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Builder
public record ManagementStockListResponse(List<ManagementStockCategoryDto> categories) {

  public static ManagementStockListResponse of(
      List<CategoryEntity> categories, Map<String, Set<String>> categoryMenuGroupingMap,
      Map<String, MenuEntity> menuMap, Map<String, StockEntity> stockMap) {

    return ManagementStockListResponse.builder()
        .categories(categories.stream()
            .map(category ->
                createCategoryDto(category, categoryMenuGroupingMap, menuMap, stockMap)
            )
            .filter(ManagementStockCategoryDto::hasMenus)
            .sorted()
            .toList()
        )
        .build();
  }

  private static ManagementStockCategoryDto createCategoryDto(
      CategoryEntity category, Map<String, Set<String>> categoryMenuGroupingMap,
      Map<String, MenuEntity> menuMap, Map<String, StockEntity> stockMap) {
    Set<String> menuIds = categoryMenuGroupingMap.getOrDefault(category.getCategoryId(), Set.of());

    List<ManagementStockMenuDto> stockMenuDtos = menuIds.stream()
        .map(menuId -> convertToMenu(menuId, menuMap))
        .filter(MenuEntity::isUsedDailyStock)
        .map(menu -> createMenuDto(menu, stockMap))
        .sorted()
        .toList();

    boolean isStockUnderThreshold = stockMenuDtos.stream()
        .filter(ManagementStockMenuDto::isNotOutOfStock)
        .anyMatch(ManagementStockMenuDto::getIsStockUnderThreshold);

    return ManagementStockCategoryDto.builder()
        .id(category.getCategoryId())
        .name(category.getName())
        .ordering(category.getOrdering())
        .isStockUnderThreshold(isStockUnderThreshold)
        .menus(stockMenuDtos)
        .build();
  }

  private static MenuEntity convertToMenu(String menuId, Map<String, MenuEntity> menuMap) {
    MenuEntity menu = menuMap.getOrDefault(menuId, MenuEntity.EMPTY);
    if (menu == MenuEntity.EMPTY) {
      throw AppException.ofBadRequest("메뉴 정보가 없습니다.");
    }
    return menu;
  }

  private static ManagementStockMenuDto createMenuDto(MenuEntity menu,
      Map<String, StockEntity> stockMap) {
    StockEntity stock = stockMap.getOrDefault(menu.getMenuId(), StockEntity.EMPTY);
    if (stock == StockEntity.EMPTY) {
      throw AppException.ofBadRequest("재고 정보가 없습니다.");
    }

    return ManagementStockMenuDto.builder()
        .id(menu.getMenuId())
        .name(menu.getName())
        .ordering(menu.getOrdering())
        .isUsedDailyStock(stock.isUsedDailyStock())
        .remainingQuantity(stock.getRemainingQuantity())
        .isStockUnderThreshold(stock.isStockUnderThreshold())
        .isOutOfStock(stock.isOutOfStock())
        .build();
  }

  @Getter
  public static class ManagementStockCategoryDto implements Comparable<ManagementStockCategoryDto> {

    private final String id;
    private final String name;
    private final int ordering;
    private final Boolean isStockUnderThreshold;
    private final List<ManagementStockMenuDto> menus;

    @Builder
    private ManagementStockCategoryDto(String id, String name, int ordering,
        boolean isStockUnderThreshold, List<ManagementStockMenuDto> menus) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.isStockUnderThreshold = isStockUnderThreshold;
      this.menus = menus;
    }

    @Override
    public int compareTo(ManagementStockCategoryDto o) {
      return ordering - o.ordering;
    }

    public boolean hasMenus() {
      return !this.menus.isEmpty();
    }

  }

  @Getter
  public static class ManagementStockMenuDto implements Comparable<ManagementStockMenuDto> {

    private final String id;
    private final String name;
    private final int ordering;
    private final Boolean isUsedDailyStock;
    private final Integer remainingQuantity;
    private final Boolean isStockUnderThreshold;
    private final Boolean isOutOfStock;

    @Builder
    private ManagementStockMenuDto(String id, String name, int ordering, Boolean isUsedDailyStock,
        Integer remainingQuantity,
        boolean isStockUnderThreshold, boolean isOutOfStock) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.isUsedDailyStock = isUsedDailyStock;
      this.remainingQuantity = remainingQuantity;
      this.isStockUnderThreshold = isStockUnderThreshold;
      this.isOutOfStock = isOutOfStock;
    }

    @JsonIgnore
    public boolean isNotOutOfStock() {
      return !isOutOfStock;
    }

    @Override
    public int compareTo(ManagementStockMenuDto o) {
      return ordering - o.ordering;
    }

  }

}
