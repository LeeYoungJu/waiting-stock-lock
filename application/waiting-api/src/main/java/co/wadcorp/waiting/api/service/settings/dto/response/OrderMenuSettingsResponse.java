package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderMenuSettingsResponse {

  private final String id;
  private final String categoryId;
  private final String name;
  private final BigDecimal unitPrice;
  private final Boolean isUsedDailyStock;
  private final Integer dailyStock;
  private final Boolean isUsedMenuQuantityPerTeam;
  private final Integer menuQuantityPerTeam;

  @Builder
  private OrderMenuSettingsResponse(String id, String categoryId, String name,
      Price unitPrice, Boolean isUsedDailyStock, Integer dailyStock,
      Boolean isUsedMenuQuantityPerTeam, Integer menuQuantityPerTeam) {
    this.id = id;
    this.categoryId = categoryId;
    this.name = name;
    this.unitPrice = unitPrice.value();
    this.isUsedDailyStock = isUsedDailyStock;
    this.dailyStock = dailyStock;
    this.isUsedMenuQuantityPerTeam = isUsedMenuQuantityPerTeam;
    this.menuQuantityPerTeam = menuQuantityPerTeam;
  }

  public static OrderMenuSettingsResponse of(MenuEntity menu, String categoryId) {
    return OrderMenuSettingsResponse.builder()
        .id(menu.getMenuId())
        .categoryId(categoryId)
        .name(menu.getName())
        .unitPrice(menu.getUnitPrice())
        .isUsedDailyStock(menu.isUsedDailyStock())
        .dailyStock(menu.getDailyStock())
        .isUsedMenuQuantityPerTeam(menu.isUsedMenuQuantityPerTeam())
        .menuQuantityPerTeam(menu.getMenuQuantityPerTeam())
        .build();
  }

}
