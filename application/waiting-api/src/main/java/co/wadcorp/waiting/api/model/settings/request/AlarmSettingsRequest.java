package co.wadcorp.waiting.api.model.settings.request;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsData;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmSettingsRequest {

  private Integer autoCancelPeriod;
  private Boolean isUsedAutoCancel;
  private Integer autoAlarmOrdering;
  private Boolean isAutoEnterAlarm;

  public AlarmSettingsEntity toEntity(String shopId) {
    return new AlarmSettingsEntity(shopId, AlarmSettingsData.of(autoCancelPeriod, isUsedAutoCancel,
        autoAlarmOrdering, isAutoEnterAlarm));
  }
}
