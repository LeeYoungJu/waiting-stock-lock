package co.wadcorp.waiting.api.internal.service.waiting.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteWaitingCheckBeforeRegisterServiceRequest {

  private String customerPhone;

  @Builder
  private RemoteWaitingCheckBeforeRegisterServiceRequest(String customerPhone) {
    this.customerPhone = customerPhone;
  }

}
