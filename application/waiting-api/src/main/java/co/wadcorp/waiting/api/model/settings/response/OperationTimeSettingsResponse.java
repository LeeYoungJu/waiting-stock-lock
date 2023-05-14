package co.wadcorp.waiting.api.model.settings.response;

import co.wadcorp.waiting.api.model.settings.vo.OperationTimeSettingsVO;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class OperationTimeSettingsResponse {

  @JsonUnwrapped
  private OperationTimeSettingsVO operationTimeSettings;

  public static OperationTimeSettingsResponse toDto(OperationTimeSettingsEntity entity) {
    OperationTimeSettingsData operationTimeSettings = entity.getOperationTimeSettingsData();
    return OperationTimeSettingsResponse.builder()
        .operationTimeSettings(OperationTimeSettingsVO.toDto(operationTimeSettings))
        .build();
  }
}
