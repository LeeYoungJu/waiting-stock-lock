package co.wadcorp.waiting.data.domain.stock;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuQuantity {

  private final String menuId;
  private final String name;
  private final int quantity;

  @Builder
  private MenuQuantity(String menuId, String name, int quantity) {
    this.menuId = menuId;
    this.name = name;
    this.quantity = quantity;
  }
}
