package co.wadcorp.waiting.api.internal.controller.waiting.dto.request;

import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingOrderValidateServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RemoteWaitingOrderValidateRequest {

  @NotNull(message = "주문 정보는 필수입니다.")
  private RemoteOrderRequest order;

  @Builder
  private RemoteWaitingOrderValidateRequest(RemoteOrderRequest order) {
    this.order = order;
  }

  public RemoteWaitingOrderValidateServiceRequest toServiceRequest() {
    return RemoteWaitingOrderValidateServiceRequest.builder()
        .order(order.toRemoteOrderDto())
        .build();
  }
}
