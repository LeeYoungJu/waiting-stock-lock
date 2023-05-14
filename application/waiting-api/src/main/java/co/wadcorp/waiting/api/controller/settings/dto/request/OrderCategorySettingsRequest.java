package co.wadcorp.waiting.api.controller.settings.dto.request;

import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCategorySettingsRequest {

  @NotBlank(message = "카테고리 ID는 필수입니다.")
  private String id;

  @NotBlank(message = "카테고리 이름은 필수입니다.")
  private String name;

  @Builder
  private OrderCategorySettingsRequest(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public OrderCategorySettingsServiceRequest toServiceRequest() {
    return OrderCategorySettingsServiceRequest.builder()
        .id(id)
        .name(name)
        .build();
  }

}
