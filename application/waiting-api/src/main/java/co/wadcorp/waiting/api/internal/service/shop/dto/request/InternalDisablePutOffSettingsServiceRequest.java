package co.wadcorp.waiting.api.internal.service.shop.dto.request;

import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InternalDisablePutOffSettingsServiceRequest {

  private boolean disablePutOff;

  @Builder
  private InternalDisablePutOffSettingsServiceRequest(boolean disablePutOff) {
    this.disablePutOff = disablePutOff;
  }

  public DisablePutOffEntity toEntity(String shopId) {
    return DisablePutOffEntity.builder()
        .shopId(shopId)
        .build();
  }

}
