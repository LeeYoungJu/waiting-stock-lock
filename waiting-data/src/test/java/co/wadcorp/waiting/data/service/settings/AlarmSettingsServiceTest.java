package co.wadcorp.waiting.data.service.settings;

import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsData;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.DefaultAlarmSettingsDataFactory;
import co.wadcorp.waiting.data.domain.settings.FakeAlarmSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlarmSettingsServiceTest {

  private FakeAlarmSettingsRepository alarmSettingsRepository;
  private AlarmSettingsService service;

  @BeforeEach
  void setup() {
    alarmSettingsRepository = new FakeAlarmSettingsRepository();
    service = new AlarmSettingsService(alarmSettingsRepository);
  }

  @Test
  @DisplayName("알림 설정 조회할 수 있다.")
  void getAlarmSettings() {
    String testShopId = "testShopId";
    AlarmSettingsEntity entity = new AlarmSettingsEntity(testShopId,
        AlarmSettingsData.of(3, true, 5));

    AlarmSettingsEntity savedEntity = alarmSettingsRepository.save(entity);

    AlarmSettingsEntity targetEntity = service.getAlarmSettings(testShopId);

    assertEquals(savedEntity, targetEntity);
  }

  @Test
  @DisplayName("저장된 알림 설정이 없다면 기본 설정으로 조회할 수 있다.")
  void get_default_AlarmSettings() {
    String testShopId = "testShopId";

    AlarmSettingsEntity defaultEntity = new AlarmSettingsEntity(testShopId, DefaultAlarmSettingsDataFactory.create());

    AlarmSettingsEntity targetEntity = service.getAlarmSettings(testShopId);

    assertEquals(defaultEntity, targetEntity);
  }

  @Test
  @DisplayName("알림 설정을 저장할 수 있다.")
  void save() {
    String testShopId = "testShopId";
    AlarmSettingsEntity targetEntity = new AlarmSettingsEntity(testShopId, DefaultAlarmSettingsDataFactory.create());

    AlarmSettingsEntity savedEntity = service.save(targetEntity);

    assertEquals(targetEntity, savedEntity);

  }

  @Test
  @DisplayName("알림 설정을 수정할 수 있다.")
  void save_update() {
    String testShopId = "testShopId";
    AlarmSettingsEntity givenEntity = new AlarmSettingsEntity(testShopId, DefaultAlarmSettingsDataFactory.create());
    service.save(givenEntity);

    AlarmSettingsEntity settingsEntity = new AlarmSettingsEntity(testShopId, AlarmSettingsData.of(7, true, 90));
    AlarmSettingsEntity savedEntity = service.save(settingsEntity);

    AlarmSettingsEntity targetEntity = service.getAlarmSettings(testShopId);

    assertEquals(savedEntity, targetEntity);

  }
}