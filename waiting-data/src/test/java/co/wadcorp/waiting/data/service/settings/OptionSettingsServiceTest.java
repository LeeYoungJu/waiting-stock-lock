package co.wadcorp.waiting.data.service.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.settings.DefaultOptionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.FakeOptionSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptionSettingsServiceTest {

  private final FakeOptionSettingsRepository OptionSettingsRepository = new FakeOptionSettingsRepository();
  private final OptionSettingsService service = new OptionSettingsService(OptionSettingsRepository);

  @Test
  @DisplayName("옵션 설정 조회할 수 있다.")
  void getOptionSettings() {
    String testShopId = "testShopId";

    List<PersonOptionSetting> personOptionSettings = createPersonOptionSettings("중학생", "성인");
    OptionSettingsData optionSettingsData = createOptionSettingsData(personOptionSettings);

    OptionSettingsEntity entity = new OptionSettingsEntity(testShopId, optionSettingsData);

    OptionSettingsEntity savedEntity = OptionSettingsRepository.save(entity);

    OptionSettingsEntity targetEntity = service.getOptionSettings(testShopId);

    assertEquals(savedEntity, targetEntity);
  }

  @Test
  @DisplayName("저장된 옵션 설정이 없다면 기본 설정으로 조회할 수 있다.")
  void get_default_OptionSettings() {
    String testShopId = "testShopId";

    OptionSettingsEntity defaultEntity = new OptionSettingsEntity(testShopId, DefaultOptionSettingDataFactory.create());

    OptionSettingsEntity targetEntity = service.getOptionSettings(testShopId);

    assertEquals(defaultEntity, targetEntity);
  }

  @Test
  @DisplayName("옵션 설정을 저장할 수 있다.")
  void save() {
    String testShopId = "testShopId";

    List<PersonOptionSetting> personOptionSettings = createPersonOptionSettings("중학생", "성인");
    OptionSettingsData optionSettingsData = createOptionSettingsData(personOptionSettings);

    OptionSettingsEntity targetEntity = new OptionSettingsEntity(testShopId, optionSettingsData);

    OptionSettingsEntity savedEntity = service.save(targetEntity);

    assertEquals(targetEntity, savedEntity);
  }

  @Test
  @DisplayName("옵션 설정을 수정할 수 있다.")
  void save_update() {
    String testShopId = "testShopId";


    List<PersonOptionSetting> givenPersonOptionSettings = createPersonOptionSettings("중학생", "성인");
    OptionSettingsData givenOptionSettingsData = createOptionSettingsData(givenPersonOptionSettings);

    OptionSettingsEntity givenEntity = new OptionSettingsEntity(testShopId, givenOptionSettingsData);
    service.save(givenEntity);

    List<PersonOptionSetting> personOptionSettings = createPersonOptionSettings("대학생", "노인");
    OptionSettingsData optionSettingsData = createOptionSettingsData(personOptionSettings);

    OptionSettingsEntity settingsEntity = new OptionSettingsEntity(testShopId, optionSettingsData);
    OptionSettingsEntity savedEntity = service.save(settingsEntity);

    OptionSettingsEntity targetEntity = service.getOptionSettings(testShopId);

    assertEquals(savedEntity, targetEntity);
  }

  public OptionSettingsData createOptionSettingsData(List<PersonOptionSetting> personOptionSettings) {
    boolean personOptionSettingUseYn = true;

    return OptionSettingsData.of(personOptionSettingUseYn, personOptionSettings);
  }

  public List<PersonOptionSetting> createPersonOptionSettings(String... personOptionNames) {
    return Arrays.stream(personOptionNames)
        .map(item -> PersonOptionSetting.builder()
            .id(UUIDUtil.shortUUID())
            .name(item)
            .isDisplayed(true)
            .isSeat(true)
            .isDefault(false)
            .canModify(true)
            .build())
        .toList();
  }

}