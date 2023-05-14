package co.wadcorp.waiting.api.model.settings.response;

import co.wadcorp.waiting.api.model.settings.vo.AlarmSettingsVO;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsData;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class AlarmSettingsResponse {

  @JsonUnwrapped
  private final AlarmSettingsVO alarmSettings;

  public static AlarmSettingsResponse toDto(AlarmSettingsEntity entity) {
    AlarmSettingsData alarmSettings = entity.getAlarmSettingsData();
    return AlarmSettingsResponse.builder()
        .alarmSettings(AlarmSettingsVO.toDto(alarmSettings))
        .build();
  }

}
