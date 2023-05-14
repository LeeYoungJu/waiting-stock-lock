package co.wadcorp.waiting.data.domain.stock;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InvalidStockMenu {

  private final String menuId;
  private final String name;
  private final int quantity;
  private final boolean isUsedDailyStock;
  private final Integer remainingQuantity;
  private final boolean isOutOfStock;

  @Builder
  public InvalidStockMenu(String menuId, String name, int quantity, boolean isUsedDailyStock,
      Integer remainingQuantity,
      boolean isOutOfStock) {
    this.menuId = menuId;
    this.name = name;
    this.quantity = quantity;
    this.isUsedDailyStock = isUsedDailyStock;
    this.remainingQuantity = remainingQuantity;
    this.isOutOfStock = isOutOfStock;
  }

}
