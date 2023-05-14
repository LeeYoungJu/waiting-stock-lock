package co.wadcorp.waiting.data.domain.settings.fixture;

import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDay;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * 오로지 테스트를 위해서 생성
 * (운영 시간을 바꿔가면서 Entity를 생성하면서 테스트 해야함)
 */
public class DefaultOperationTimeSettingDataFixture {

  private static final String PAUSE_REASON_UUID = "9gcl4qqmT9OcuVYdKd_Pzw";

  /**
   * OperationTimeForDay
   * */
  private static LocalTime OPERATION_START_TIME = LocalTime.of(10, 0, 0);
  private static LocalTime OPERATION_END_TIME = LocalTime.of(20, 0 ,0);

  /**
   * AutoPauseSettings
   * */
  private static LocalTime AUTO_PAUSE_START_TIME = LocalTime.of(14,0,0);
  private static LocalTime AUTO_PAUSE_END_TIME = LocalTime.of(15,0,0);


  public static OperationTimeSettingsData create() {
    return OperationTimeSettingsData.builder()
        .operationTimeForDays(createOperationTimeForAllDay())
        .autoPauseSettings(createAutoPauseSettings())
        .isUsedAutoPause(false)
        .build();
  }

  public static void changeOperationTime(LocalTime operationStartTime, LocalTime operationEndTime) {
    OPERATION_START_TIME = operationStartTime;
    OPERATION_END_TIME = operationEndTime;
  }

  public static PauseReason createPauseReason() {
    return new PauseReason(PAUSE_REASON_UUID, true, "웨이팅이 잠시 정지되었어요. 잠시만 기다려주세요.");
  }

  private static List<OperationTimeForDay> createOperationTimeForAllDay() {
    return Arrays.stream(OperationDay.values())
        .map(DefaultOperationTimeSettingDataFixture::createOperationTimeForDay)
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


  private static AutoPauseSettings createAutoPauseSettings() {
    return AutoPauseSettings.builder()
        .autoPauseStartTime(AUTO_PAUSE_START_TIME)
        .autoPauseEndTime(AUTO_PAUSE_END_TIME)
        .pauseReasons(List.of(createPauseReason()))
        .build();
  }
}