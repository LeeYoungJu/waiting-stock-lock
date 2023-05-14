package co.wadcorp.waiting.api.controller.waiting.register.dto.request;

import co.wadcorp.waiting.api.service.waiting.register.dto.request.MenuType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RegisterWaitingOrderMenuRequest {

  @NotNull(message = "메뉴 타입은 필수입니다.")
  private MenuType menuType;


  public DisplayMappingType getDisplayMappingType() {
    return this.menuType.getDisplayMappingType();
  }
}
