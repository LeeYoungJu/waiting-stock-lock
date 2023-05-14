package co.wadcorp.waiting.api.service.waiting.register.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterValidateMenuResponse {

  private String reason;
  private List<InvalidMenu> menus;

  public RegisterValidateMenuResponse() {
  }

  @Builder
  public RegisterValidateMenuResponse(String reason, List<InvalidMenu> menus) {
    this.reason = reason;
    this.menus = menus;
  }

  @Getter
  public static class InvalidMenu {

    private final String id;
    private final String name;
    private final int quantity;
    private final Boolean isUsedDailyStock;
    private final Integer remainingQuantity;
    private final Boolean isOutOfStock;

    @Builder
    public InvalidMenu(String id, String name, int quantity, Boolean isUsedDailyStock, Integer remainingQuantity,
        boolean isOutOfStock) {
      this.id = id;
      this.name = name;
      this.quantity = quantity;
      this.isUsedDailyStock = isUsedDailyStock;
      this.remainingQuantity = remainingQuantity;
      this.isOutOfStock = isOutOfStock;
    }

  }

}
