package co.wadcorp.waiting.data.domain.settings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlarmSettingsEntityTest {

  private static final int DEFAULT_AUTO_CANCEL_PERIOD = 3;
  private static final boolean DEFAULT_IS_USED_AUTO_CANCEL = true;
  private static final int DEFAULT_AUTO_ALARM_ORDERING = 3;

  @Test
  @DisplayName("알림 설정에 is_auto_enter_alarm 값이 null인 경우는 default값인 true로 조회되어야 한다.")
  void isUsedAutoEnterAlarm_null() {
    //Given
    AlarmSettingsData alarmSettingsData = create_alarm_settings_data(
        DEFAULT_AUTO_CANCEL_PERIOD,
        DEFAULT_IS_USED_AUTO_CANCEL,
        DEFAULT_AUTO_ALARM_ORDERING,
        null);

    //When
    AlarmSettingsEntity alarmSettingsEntity = new AlarmSettingsEntity("test", alarmSettingsData);

    //Then
    assertTrue(
      alarmSettingsEntity.isUsedAutoEnterAlarm()
    );
  }

  @Test
  @DisplayName("알림 설정에 is_auto_enter_alarm 값이 정상적으로 들어가있으면 그 값이 그대로 조회되어야 한다.")
  void isUsedAutoEnterAlarm_normal() {
    //Given
    AlarmSettingsData isAutoEnterAlarmTrueData = create_alarm_settings_data(
        DEFAULT_AUTO_CANCEL_PERIOD,
        DEFAULT_IS_USED_AUTO_CANCEL,
        DEFAULT_AUTO_ALARM_ORDERING,
        true);
    AlarmSettingsData isAutoEnterAlarmFalseData = create_alarm_settings_data(
        DEFAULT_AUTO_CANCEL_PERIOD,
        DEFAULT_IS_USED_AUTO_CANCEL,
        DEFAULT_AUTO_ALARM_ORDERING,
        false);

    //When
    AlarmSettingsEntity isAutoEnterAlarmTrueEntity = new AlarmSettingsEntity("trueTest", isAutoEnterAlarmTrueData);
    AlarmSettingsEntity isAutoEnterAlarmFalseEntity = new AlarmSettingsEntity("falseTest", isAutoEnterAlarmFalseData);

    //Then
    assertTrue(
        isAutoEnterAlarmTrueEntity.isUsedAutoEnterAlarm()
    );
    assertFalse(
        isAutoEnterAlarmFalseEntity.isUsedAutoEnterAlarm()
    );
  }

  private AlarmSettingsData create_alarm_settings_data(Integer autoCancelPeriod,
      Boolean isUsedAutoCancel,
      Integer autoAlarmOrdering,
      Boolean isAutoEnterAlarm) {
    return AlarmSettingsData.of(autoCancelPeriod, isUsedAutoCancel, autoAlarmOrdering, isAutoEnterAlarm);
  }

}