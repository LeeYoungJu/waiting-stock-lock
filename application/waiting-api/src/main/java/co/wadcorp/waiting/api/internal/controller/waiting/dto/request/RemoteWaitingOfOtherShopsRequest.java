package co.wadcorp.waiting.api.internal.controller.waiting.dto.request;

import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingOfOtherShopsServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteWaitingOfOtherShopsRequest {

  @NotEmpty(message = "전화번호는 필수입니다.")
  private String customerPhone;

  @Builder
  private RemoteWaitingOfOtherShopsRequest(String customerPhone) {
    this.customerPhone = customerPhone;
  }

  public RemoteWaitingOfOtherShopsServiceRequest toServiceRequest() {
    return RemoteWaitingOfOtherShopsServiceRequest.builder()
        .customerPhone(customerPhone)
        .build();
  }

}
