package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.exception.AppException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
public record OrderMenuSettingsListResponse(List<OrderCategoryDto> categories) {

  public static OrderMenuSettingsListResponse of(
      List<CategoryEntity> categories,
      Map<String, Set<String>> categoryMenuGroupingMap,
      Map<String, MenuEntity> menuMap
  ) {
    return OrderMenuSettingsListResponse.builder()
        .categories(categories.stream()
            .map(category -> createCategoryDto(category, categoryMenuGroupingMap, menuMap))
            .sorted()
            .toList()
        )
        .build();
  }

  private static OrderCategoryDto createCategoryDto(
      CategoryEntity category,
      Map<String, Set<String>> categoryMenuGroupingMap,
      Map<String, MenuEntity> menuMap
  ) {
    Set<String> menuIds = categoryMenuGroupingMap.getOrDefault(category.getCategoryId(),
        Set.of());

    return OrderCategoryDto.builder()
        .id(category.getCategoryId())
        .name(category.getName())
        .ordering(category.getOrdering())
        .menus(menuIds.stream()
            .map(menuId -> createMenuDto(menuId, menuMap))
            .sorted()
            .toList()
        )
        .build();
  }

  private static OrderMenuDto createMenuDto(String menuId, Map<String, MenuEntity> menuMap) {
    MenuEntity menu = menuMap.getOrDefault(menuId, MenuEntity.EMPTY);
    if (menu == MenuEntity.EMPTY) {
      throw AppException.ofBadRequest("메뉴 정보가 없습니다.");
    }

    return OrderMenuDto.builder()
        .id(menuId)
        .name(menu.getName())
        .ordering(menu.getOrdering())
        .unitPrice(menu.getUnitPrice().value())
        .isUsedDailyStock(menu.isUsedDailyStock())
        .dailyStock(menu.getDailyStock())
        .isUsedMenuQuantityPerTeam(menu.isUsedMenuQuantityPerTeam())
        .menuQuantityPerTeam(menu.getMenuQuantityPerTeam())
        .build();
  }

  @Getter
  @EqualsAndHashCode(of = "id")
  public static class OrderCategoryDto implements Comparable<OrderCategoryDto> {

    private final String id;
    private final String name;
    private final int ordering;
    private final List<OrderMenuDto> menus;

    @Builder
    private OrderCategoryDto(String id, String name, int ordering, List<OrderMenuDto> menus) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.menus = menus;
    }

    @Override
    public int compareTo(OrderCategoryDto o) {
      return ordering - o.ordering;
    }

  }

  @Getter
  @EqualsAndHashCode(of = "id")
  public static class OrderMenuDto implements Comparable<OrderMenuDto> {

    private final String id;
    private final String name;
    private final int ordering;
    private final BigDecimal unitPrice;
    private final Boolean isUsedDailyStock;
    private final Integer dailyStock;
    private final Boolean isUsedMenuQuantityPerTeam;
    private final Integer menuQuantityPerTeam;

    @Builder
    private OrderMenuDto(String id, String name, int ordering, BigDecimal unitPrice,
        Boolean isUsedDailyStock, Integer dailyStock, Boolean isUsedMenuQuantityPerTeam,
        Integer menuQuantityPerTeam) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.unitPrice = unitPrice;
      this.isUsedDailyStock = isUsedDailyStock;
      this.dailyStock = dailyStock;
      this.isUsedMenuQuantityPerTeam = isUsedMenuQuantityPerTeam;
      this.menuQuantityPerTeam = menuQuantityPerTeam;
    }

    @Override
    public int compareTo(OrderMenuDto o) {
      return ordering - o.ordering;
    }

  }

}
