package co.wadcorp.waiting.api.internal.service.waiting.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteWaitingOfOtherShopsServiceRequest {

  private String customerPhone;

  @Builder
  private RemoteWaitingOfOtherShopsServiceRequest(String customerPhone) {
    this.customerPhone = customerPhone;
  }

}
