package co.wadcorp.waiting.api.internal.controller.shop.dto.request;

import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalDisablePutOffSettingsServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InternalDisablePutOffSettingsRequest {

  @NotNull(message = "미루기 off 여부는 필수입니다.")
  private Boolean disablePutOff;

  @Builder
  private InternalDisablePutOffSettingsRequest(Boolean disablePutOff) {
    this.disablePutOff = disablePutOff;
  }

  public InternalDisablePutOffSettingsServiceRequest toServiceRequest() {
    return InternalDisablePutOffSettingsServiceRequest.builder()
        .disablePutOff(disablePutOff)
        .build();
  }

}
