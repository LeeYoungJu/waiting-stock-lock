package co.wadcorp.waiting.api.controller.settings.dto.request;

import co.wadcorp.waiting.api.service.settings.dto.request.MenuType;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderDisplayMenuServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDisplayMenuRequest {

  @NotNull(message = "메뉴판 타입은 필수입니다.")
  private MenuType menuType;

  public OrderDisplayMenuServiceRequest toServiceRequest() {
    return OrderDisplayMenuServiceRequest.builder()
        .menuType(menuType)
        .build();
  }

}
