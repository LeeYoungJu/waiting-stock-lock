package co.wadcorp.waiting.api.internal.controller.waiting.dto.request;

import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingCheckBeforeRegisterServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteWaitingCheckBeforeRegisterRequest {

  @NotEmpty(message = "전화번호는 필수입니다.")
  private String customerPhone;

  @Builder
  private RemoteWaitingCheckBeforeRegisterRequest(String customerPhone) {
    this.customerPhone = customerPhone;
  }

  public RemoteWaitingCheckBeforeRegisterServiceRequest toServiceRequest() {
    return RemoteWaitingCheckBeforeRegisterServiceRequest.builder()
        .customerPhone(customerPhone)
        .build();
  }

}
