package co.wadcorp.waiting.data.domain.settings;

public class DefaultAlarmSettingsDataFactory {

  private static final int DEFAULT_AUTO_CANCEL_PERIOD = 3;
  private static final boolean DEFAULT_IS_USED_AUTO_CANCEL = true;
  private static final int DEFAULT_AUTO_ALARM_ORDERING = 3;
  private static final boolean DEFAULT_IS_AUTO_ENTER_ALARM = true;

  public static AlarmSettingsData create() {
    return AlarmSettingsData.of(DEFAULT_AUTO_CANCEL_PERIOD, DEFAULT_IS_USED_AUTO_CANCEL,
        DEFAULT_AUTO_ALARM_ORDERING, DEFAULT_IS_AUTO_ENTER_ALARM);
  }

}
