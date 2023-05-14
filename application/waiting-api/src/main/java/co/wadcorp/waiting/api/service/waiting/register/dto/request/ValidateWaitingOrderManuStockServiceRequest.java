package co.wadcorp.waiting.api.service.waiting.register.dto.request;

import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ValidateWaitingOrderManuStockServiceRequest {

  private final List<Menu> menus;

  @Builder
  public ValidateWaitingOrderManuStockServiceRequest(List<Menu> menus) {
    this.menus = menus;
  }

  public List<MenuQuantity> toMenuQuantity() {
    return menus.stream()
        .map(item -> MenuQuantity.builder()
            .menuId(item.id)
            .name(item.name)
            .quantity(item.quantity)
            .build())
        .toList();
  }

  @Getter
  public static class Menu {
    private final String id;
    private final String name;
    private final int quantity;

    @Builder
    public Menu(String id, String name, int quantity) {
      this.id = id;
      this.name = name;
      this.quantity = quantity;
    }
  }
}
