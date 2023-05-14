package co.wadcorp.waiting.api.model.settings.vo;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsData;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AlarmSettingsVO {

  private final Integer autoCancelPeriod;
  private final Boolean isUsedAutoCancel;
  private final Integer autoAlarmOrdering;
  private final Boolean isAutoEnterAlarm;

  @Builder
  public AlarmSettingsVO(Integer autoCancelPeriod, Boolean isUsedAutoCancel,
      Integer autoAlarmOrdering, Boolean isAutoEnterAlarm) {
    this.autoCancelPeriod = autoCancelPeriod;
    this.isUsedAutoCancel = isUsedAutoCancel;
    this.autoAlarmOrdering = autoAlarmOrdering;
    this.isAutoEnterAlarm = isAutoEnterAlarm;
  }

  public static AlarmSettingsVO toDto(AlarmSettingsData alarmSettings) {
    alarmSettings.setDefaultIsAutoEnterAlarmIfNotExist();

    return new AlarmSettingsVO(alarmSettings.getAutoCancelPeriod(),
        alarmSettings.getIsUsedAutoCancel(),
        alarmSettings.getAutoAlarmOrdering(),
        alarmSettings.getIsAutoEnterAlarm());
  }

}
