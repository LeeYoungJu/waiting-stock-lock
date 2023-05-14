package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import lombok.Builder;

public class OrderDisplayMenuServiceRequest {

  private final MenuType menuType;

  @Builder
  private OrderDisplayMenuServiceRequest(MenuType menuType) {
    this.menuType = menuType;
  }

  public DisplayMappingType getDisplayMappingType() {
    return menuType.getDisplayMappingType();
  }

}
