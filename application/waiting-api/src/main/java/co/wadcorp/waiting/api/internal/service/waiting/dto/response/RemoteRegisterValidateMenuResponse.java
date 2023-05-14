package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteRegisterValidateMenuResponse {

  private String reason;
  private List<RemoteInvalidMenu> menus;

  @Builder
  private RemoteRegisterValidateMenuResponse(String reason, List<RemoteInvalidMenu> menus) {
    this.reason = reason;
    this.menus = menus;
  }

  @Getter
  public static class RemoteInvalidMenu {

    private final String id;
    private final String name;
    private final int quantity;
    private final Integer remainingQuantity;
    private final Boolean isOutOfStock;

    @Builder
    public RemoteInvalidMenu(String id, String name, int quantity, Boolean isUsedDailyStock, Integer remainingQuantity,
        boolean isOutOfStock) {
      this.id = id;
      this.name = name;
      this.quantity = quantity;
      this.remainingQuantity = remainingQuantity;
      this.isOutOfStock = isOutOfStock;
    }

    public static RemoteInvalidMenu of(InvalidStockMenu invalidStockMenu) {
      return RemoteInvalidMenu.builder()
          .id(invalidStockMenu.getMenuId())
          .name(invalidStockMenu.getName())
          .quantity(invalidStockMenu.getQuantity())
          .remainingQuantity(invalidStockMenu.getRemainingQuantity())
          .build();
    }

  }
}
