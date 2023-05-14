package co.wadcorp.waiting.api.controller.settings.dto.request;


import co.wadcorp.waiting.api.service.settings.dto.request.OrderSettingsServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderSettingsRequest {

  @NotNull(message = "주문 설정 사용 여부는 필수입니다.")
  private Boolean isPossibleOrder;

  @Builder
  private OrderSettingsRequest(Boolean isPossibleOrder) {
    this.isPossibleOrder = isPossibleOrder;
  }

  public OrderSettingsServiceRequest toServiceRequest() {
    return OrderSettingsServiceRequest.builder()
        .isPossibleOrder(isPossibleOrder)
        .build();
  }

}
