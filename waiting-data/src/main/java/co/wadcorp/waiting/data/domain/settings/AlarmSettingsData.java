package co.wadcorp.waiting.data.domain.settings;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class AlarmSettingsData {

  private static final int DEFAULT_AUTO_CANCEL_PERIOD = 3;
  private static final boolean DEFAULT_IS_USED_AUTO_CANCEL = true;
  private static final int DEFAULT_AUTO_ALARM_ORDERING = 1;
  private static final boolean DEFAULT_IS_AUTO_ENTER_ALARM = true;

  private Integer autoCancelPeriod;
  private Boolean isUsedAutoCancel;
  private Integer autoAlarmOrdering;
  private Boolean isAutoEnterAlarm;

  private AlarmSettingsData() {
  }

  public static AlarmSettingsData of(Integer autoCancelPeriod, Boolean isUsedAutoCancel,
      Integer autoAlarmOrdering) {
    AlarmSettingsDataValidator.validate(autoCancelPeriod, autoAlarmOrdering);

    AlarmSettingsData result = new AlarmSettingsData();
    result.autoCancelPeriod = autoCancelPeriod;
    result.isUsedAutoCancel = isUsedAutoCancel;
    result.autoAlarmOrdering = autoAlarmOrdering;
    result.isAutoEnterAlarm = DEFAULT_IS_AUTO_ENTER_ALARM;

    return result;
  }

  public static AlarmSettingsData of(Integer autoCancelPeriod, Boolean isUsedAutoCancel,
      Integer autoAlarmOrdering, Boolean isAutoEnterAlarm) {
    AlarmSettingsDataValidator.validate(autoCancelPeriod, autoAlarmOrdering);

    AlarmSettingsData result = new AlarmSettingsData();
    result.autoCancelPeriod = autoCancelPeriod;
    result.isUsedAutoCancel = isUsedAutoCancel;
    result.autoAlarmOrdering = autoAlarmOrdering;
    result.isAutoEnterAlarm = isAutoEnterAlarm;

    return result;
  }

  /**
   * <pre>
   * AlarmSettingsData의 isAutoEnterAlarm 필드는 뒤늦게 추가된 필드이기 때문에 기존에 저장된 데이터에는 이 필드가 존재하지 않는다.
   * 그래서 null 체크 후 null 이면 기본값(true)을 세팅해줄 필요가 있다.
   * </pre>
   */
  public void setDefaultIsAutoEnterAlarmIfNotExist() {
    if(doesNeedToSetDefault()) {
      setDefaultIsAutoEnterAlarm();
    }
  }

  private boolean doesNeedToSetDefault() {
    return this.isAutoEnterAlarm == null;
  }

  private void setDefaultIsAutoEnterAlarm() {
    this.isAutoEnterAlarm = DEFAULT_IS_AUTO_ENTER_ALARM;
  }

}
