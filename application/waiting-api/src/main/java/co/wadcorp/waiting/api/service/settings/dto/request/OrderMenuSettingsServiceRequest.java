package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderMenuSettingsServiceRequest {

  private String id;
  private String categoryId;
  private String name;
  private Price unitPrice;
  private boolean isUsedDailyStock;
  private Integer dailyStock;
  private boolean isUsedMenuQuantityPerTeam;
  private Integer menuQuantityPerTeam;

  @Builder
  private OrderMenuSettingsServiceRequest(String id, String categoryId, String name,
      BigDecimal unitPrice, Boolean isUsedDailyStock, Integer dailyStock,
      Boolean isUsedMenuQuantityPerTeam, Integer menuQuantityPerTeam) {
    this.id = id;
    this.categoryId = categoryId;
    this.name = name;
    this.unitPrice = Price.of(unitPrice);
    this.isUsedDailyStock = isUsedDailyStock;
    this.dailyStock = dailyStock;
    this.isUsedMenuQuantityPerTeam = isUsedMenuQuantityPerTeam;
    this.menuQuantityPerTeam = menuQuantityPerTeam;
  }

  public MenuEntity toEntity(String shopId, int ordering) {
    return MenuEntity.builder()
        .menuId(id)
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .unitPrice(unitPrice)
        .isUsedDailyStock(isUsedDailyStock)
        .dailyStock(isUsedDailyStock
            ? dailyStock
            : 0
        )
        .isUsedMenuQuantityPerTeam(isUsedMenuQuantityPerTeam)
        .menuQuantityPerTeam(isUsedMenuQuantityPerTeam
            ? menuQuantityPerTeam
            : 0
        )
        .build();
  }

}
