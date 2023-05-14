package co.wadcorp.waiting.data.domain.settings.fixture;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDay;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class OperationTimeSettingDataFixture {

  private static final String PAUSE_REASON_UUID = UUIDUtil.shortUUID();

  /**
   * OperationTimeForDay
   * */
  private static final LocalTime OPERATION_START_TIME = LocalTime.of(10, 0, 0);
  private static final LocalTime OPERATION_END_TIME = LocalTime.of(20, 0 ,0);

  /**
   * AutoPauseSettings
   * */
  private static final LocalTime AUTO_PAUSE_START_TIME = LocalTime.of(14,0,0);
  private static final LocalTime AUTO_PAUSE_END_TIME = LocalTime.of(15,0,0);


  public static OperationTimeSettingsData closedDayFixture(String uuid) {
    return OperationTimeSettingsData.builder()
        .operationTimeForDays(createOperationTimeForAllClosedDay())
        .isUsedAutoPause(false)
        .autoPauseSettings(createAutoPauseSettings(uuid))
        .build();
  }



  public static OperationTimeSettingsData pauseFixture(String uuid) {
    return OperationTimeSettingsData.builder()
        .operationTimeForDays(createOperationTimeForAllDay())
        .isUsedAutoPause(true)
        .autoPauseSettings(createAutoPauseSettings(uuid))
        .build();
  }


  public static PauseReason createPauseReason(String uuid) {
    return new PauseReason(uuid, true, "웨이팅이 잠시 정지되었어요. 잠시만 기다려주세요.");
  }


  private static List<OperationTimeForDay> createOperationTimeForAllClosedDay() {
    return Arrays.stream(OperationDay.values())
        .map(OperationTimeSettingDataFixture::createOperationTimeForClosedDay)
        .toList();
  }

  private static OperationTimeForDay createOperationTimeForClosedDay(OperationDay day) {
    return OperationTimeForDay.builder()
        .day(String.valueOf(day))
        .operationStartTime(OPERATION_START_TIME)
        .operationEndTime(OPERATION_END_TIME)
        .isClosedDay(true)
        .build();
  }

  private static List<OperationTimeForDay> createOperationTimeForAllDay() {
    return Arrays.stream(OperationDay.values())
        .map(OperationTimeSettingDataFixture::createOperationTimeForDay)
        .toList();
  }

  private static OperationTimeForDay createOperationTimeForDay(OperationDay day) {
    return OperationTimeForDay.builder()
        .day(String.valueOf(day))
        .operationStartTime(OPERATION_START_TIME)
        .operationEndTime(OPERATION_END_TIME)
        .isClosedDay(false)
        .build();
  }


  private static AutoPauseSettings createAutoPauseSettings(String uuid) {
    return AutoPauseSettings.builder()
        .autoPauseStartTime(AUTO_PAUSE_START_TIME)
        .autoPauseEndTime(AUTO_PAUSE_END_TIME)
        .pauseReasons(List.of(createPauseReason(uuid)))
        .build();
  }


}
