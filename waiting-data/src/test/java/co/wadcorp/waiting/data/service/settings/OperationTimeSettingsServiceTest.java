package co.wadcorp.waiting.data.service.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.settings.DefaultOperationTimeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OperationTimeForDaysChangeChecker;
import co.wadcorp.waiting.data.domain.settings.fixture.DefaultOperationTimeSettingDataFixture;
import co.wadcorp.waiting.data.domain.settings.FakeOperationTimeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OperationTimeSettingsServiceTest extends IntegrationTest {

  @Autowired
  private OperationTimeSettingsService operationTimeSettingsService;

  private FakeOperationTimeSettingsRepository repository = new FakeOperationTimeSettingsRepository();

  @Test
  void getOperationTimeSettings() {
    String shopId = "shopId";
    OperationTimeSettingsEntity entity = OperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationTimeSettingsData(DefaultOperationTimeSettingDataFactory.create())
        .build();

    OperationTimeSettingsEntity save = repository.save(entity);
    OperationTimeSettingsEntity target = repository.findFirstByShopIdAndIsPublished(shopId, true)
        .orElse(OperationTimeSettingsEntity.builder().build());

    assertEquals(save, target);

  }

  @Test
  void saveOperationTimeSettings() {
  }

  @Test
  @DisplayName("2개 이상의 동일한 정보의 OperationTimeSettingsEntity가 저장되어 있을 때 비교")
  void isThereChangeInOperationTime() {
    //Given
    String shopId = "shopId";
    IntStream.range(0, 3).forEach(i -> {
      OperationTimeSettingsEntity operationTimeSettings = OperationTimeSettingsEntity.builder()
          .shopId(shopId)
          .operationTimeSettingsData(DefaultOperationTimeSettingDataFixture.create())
          .build();

      operationTimeSettingsService.saveOperationTimeSettings(operationTimeSettings);
    });

    //When
    OperationTimeForDaysChangeChecker changeChecker =
        operationTimeSettingsService.isThereChangeInOperationTime(shopId);

    //Then
    assertFalse(Arrays.stream(OperationDay.values())
        .allMatch(day -> changeChecker.isThereChangeInDay(String.valueOf(day))));
  }

  @Test
  @DisplayName("두개 이상의 서로 다른 OperationTimeSettingsEntity가 저장되어 있을 때 비교")
  void isThereChangeInOperationTime_Diff() {
    //Given
    String shopId = "shopId";
    IntStream.range(0, 2).forEach(i -> {
      DefaultOperationTimeSettingDataFixture.changeOperationTime(LocalTime.of(i, i), LocalTime.of(5+i, 30+i));

      OperationTimeSettingsEntity operationTimeSettings = OperationTimeSettingsEntity.builder()
          .shopId(shopId)
          .operationTimeSettingsData(DefaultOperationTimeSettingDataFixture.create())
          .build();

      operationTimeSettingsService.saveOperationTimeSettings(operationTimeSettings);
    });

    //When
    OperationTimeForDaysChangeChecker changeChecker =
        operationTimeSettingsService.isThereChangeInOperationTime(shopId);

    //Then
    assertTrue(Arrays.stream(OperationDay.values())
        .anyMatch(day -> changeChecker.isThereChangeInDay(String.valueOf(day))));
  }

  @Test
  @DisplayName("하나의 OperationTimeSettingsEntity가 저장되어 있을 때 비교")
  void isThereChangeInOperationTime_JustOne() {
    //Given
    String shopId = "shopId";
    OperationTimeSettingsEntity operationTimeSettings = OperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationTimeSettingsData(DefaultOperationTimeSettingDataFixture.create())
        .build();
    operationTimeSettingsService.saveOperationTimeSettings(operationTimeSettings);

    //When
    OperationTimeForDaysChangeChecker changeChecker = operationTimeSettingsService.isThereChangeInOperationTime(
        shopId);

    //Then
    assertTrue(Arrays.stream(OperationDay.values())
        .anyMatch(day -> changeChecker.isThereChangeInDay(String.valueOf(day))));
  }


}