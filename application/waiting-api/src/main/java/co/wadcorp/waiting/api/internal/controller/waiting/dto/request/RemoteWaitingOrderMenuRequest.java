package co.wadcorp.waiting.api.internal.controller.waiting.dto.request;

import co.wadcorp.waiting.api.service.waiting.register.dto.request.MenuType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RemoteWaitingOrderMenuRequest {

  @NotNull(message = "메뉴 타입은 필수입니다.")
  private MenuType menuType;

  @Builder
  private RemoteWaitingOrderMenuRequest(MenuType menuType) {
    this.menuType = menuType;
  }

  public DisplayMappingType getDisplayMappingType() {
    return menuType.getDisplayMappingType();
  }

}
