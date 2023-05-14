package co.wadcorp.waiting.data.domain.settings;

import static co.wadcorp.waiting.data.enums.WaitingModeType.DEFAULT;

import co.wadcorp.waiting.data.enums.WaitingModeType;
import java.util.List;

public class DefaultHomeSettingDataFactory {

  private static final WaitingModeType DEFAULT_WAITING_MODE_TYPE = DEFAULT;

  private static final String DEFAULT_UUID = "7oqN4jesQuufPMCLmgVuug";
  private static final String TABLE1_UUID = "tZCcLfzXQyGI6hKo8tVLrw";
  private static final String TABLE2_UUID = "XVxg4bLoTcGO5qtrhqZ57A";

  /**
   * DefaultModeSetting
   * */
  private static final int MIN_SEAT_COUNT = 2;
  private static final int MAX_SEAT_COUNT = 10;
  private static final int EXPECTED_WAITING_PERIOD = 5;

  /**
  * TableModeSetting
  * */
  private static final int TABLE_MODE_MIN_SEAT_COUNT = 1;
  private static final int TABLE_MODE_MAX_SEAT_COUNT = 10;
  private static final int TABLE_MODE_EXPECTED_WAITING_PERIOD = 5;


  public static HomeSettingsData create() {
    return HomeSettingsData.builder()
        .waitingModeType(DEFAULT_WAITING_MODE_TYPE.name())
        .defaultModeSettings(createDefaultModeSettings())
        .tableModeSettings(createTableModeSettings())
        .build();
  }

  private static SeatOptions createDefaultModeSettings() {
    return SeatOptions.builder()
        .id(DEFAULT_UUID)
        .name(SeatOptions.DEFAULT_MODE_NOT_TAKE_OUT_NAME_TEXT)
        .minSeatCount(MIN_SEAT_COUNT)
        .maxSeatCount(MAX_SEAT_COUNT)
        .expectedWaitingPeriod(EXPECTED_WAITING_PERIOD)
        .isUsedExpectedWaitingPeriod(true)
        .isDefault(true)
        .isTakeOut(false)
        .build();
  }

  private static List<SeatOptions> createTableModeSettings() {
    return List.of(createTableModeSlot1(), createTableModeSlot2());
  }

  private static SeatOptions createTableModeSlot1() {
    return SeatOptions.builder()
        .id(TABLE1_UUID)
        .name("홀 2인석")
        .minSeatCount(TABLE_MODE_MIN_SEAT_COUNT)
        .maxSeatCount(TABLE_MODE_MAX_SEAT_COUNT)
        .expectedWaitingPeriod(TABLE_MODE_EXPECTED_WAITING_PERIOD)
        .isUsedExpectedWaitingPeriod(true)
        .isDefault(true)
        .isTakeOut(false)
        .build();
  }
  private static SeatOptions createTableModeSlot2() {
    return SeatOptions.builder()
        .id(TABLE2_UUID)
        .name("홀 4인석")
        .minSeatCount(TABLE_MODE_MIN_SEAT_COUNT)
        .maxSeatCount(TABLE_MODE_MAX_SEAT_COUNT)
        .expectedWaitingPeriod(EXPECTED_WAITING_PERIOD)
        .isUsedExpectedWaitingPeriod(true)
        .isDefault(true)
        .isTakeOut(false)
        .build();
  }

}
