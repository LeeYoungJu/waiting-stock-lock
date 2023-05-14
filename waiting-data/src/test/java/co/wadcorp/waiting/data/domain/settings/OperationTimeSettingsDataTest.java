package co.wadcorp.waiting.data.domain.settings;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.wadcorp.waiting.data.domain.settings.fixture.DefaultOperationTimeSettingDataFixture;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OperationTimeSettingsDataTest {

  @Test
  @DisplayName("동일한 운영시간설정 데이터 비교")
  void isSameOperationTimeForDays_withSame() {
    //Given
    OperationTimeSettingsData operationTimeSettingsData1 =
        DefaultOperationTimeSettingDataFixture.create();
    OperationTimeSettingsData operationTimeSettingsData2 =
        DefaultOperationTimeSettingDataFixture.create();

    //When
    OperationTimeForDaysChangeChecker changeChecker = operationTimeSettingsData1
        .checkChangesInTimeForDays(operationTimeSettingsData2);

    //Then
    assertFalse(Arrays.stream(OperationDay.values())
        .allMatch(day -> changeChecker.isThereChangeInDay(String.valueOf(day))));
  }

  @Test
  @DisplayName("서로 다른 운영시간설정 데이터 비교")
  void isSameOperationTimeForDays_withDiff() {
    //Given
    DefaultOperationTimeSettingDataFixture.changeOperationTime(LocalTime.of(11, 0),
        LocalTime.of(17, 0));
    OperationTimeSettingsData operationTimeSettingsData1 =
        DefaultOperationTimeSettingDataFixture.create();

    DefaultOperationTimeSettingDataFixture.changeOperationTime(LocalTime.of(10, 0),
        LocalTime.of(17, 0));
    OperationTimeSettingsData operationTimeSettingsData2 =
        DefaultOperationTimeSettingDataFixture.create();

    //When
    OperationTimeForDaysChangeChecker changeChecker = operationTimeSettingsData1
        .checkChangesInTimeForDays(operationTimeSettingsData2);

    //Then
    assertTrue(Arrays.stream(OperationDay.values())
        .anyMatch(day -> changeChecker.isThereChangeInDay(String.valueOf(day))));
  }

  @Test
  @DisplayName("비교 타겟에 null이 들어가는 경우")
  void isSameOperationTimeForDays_withNull() {
    //Given
    DefaultOperationTimeSettingDataFixture.changeOperationTime(LocalTime.of(11, 0),
        LocalTime.of(17, 0));
    OperationTimeSettingsData operationTimeSettingsData1 =
        DefaultOperationTimeSettingDataFixture.create();

    //When
    OperationTimeForDaysChangeChecker changeChecker = operationTimeSettingsData1
        .checkChangesInTimeForDays(null);

    //Then
    assertTrue(Arrays.stream(OperationDay.values())
        .anyMatch(day -> changeChecker.isThereChangeInDay(String.valueOf(day))));
  }

}