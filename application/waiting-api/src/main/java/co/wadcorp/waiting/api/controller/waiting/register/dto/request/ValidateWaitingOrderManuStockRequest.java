package co.wadcorp.waiting.api.controller.waiting.register.dto.request;

import co.wadcorp.waiting.api.service.waiting.register.dto.request.ValidateWaitingOrderManuStockServiceRequest;
import co.wadcorp.waiting.api.service.waiting.register.dto.request.ValidateWaitingOrderManuStockServiceRequest.Menu;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ValidateWaitingOrderManuStockRequest {

  private List<Menus> menus;

  public ValidateWaitingOrderManuStockRequest() {
  }

  @Builder
  public ValidateWaitingOrderManuStockRequest(List<Menus> menus) {
    this.menus = menus;
  }

  public ValidateWaitingOrderManuStockServiceRequest toServiceRequest() {
    return ValidateWaitingOrderManuStockServiceRequest.builder()
        .menus(this.menus.stream()
            .map(menu -> Menu
                .builder()
                .id(menu.id)
                .name(menu.name)
                .quantity(menu.quantity)
                .build())
            .toList())
        .build();
  }

  @Getter
  public static class Menus {
    private String id;
    private String name;
    private int quantity;

    public Menus() {
    }

    @Builder
    public Menus(String id, String name, int quantity) {
      this.id = id;
      this.name = name;
      this.quantity = quantity;
    }
  }
}
