package co.wadcorp.waiting.data.service.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.data.domain.settings.DefaultPrecautionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.FakePrecautionRepository;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsData;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrecautionSettingsServiceTest {

  private final PrecautionSettingsRepository precautionSettingsRepository = new FakePrecautionRepository();
  private final PrecautionSettingsService service = new PrecautionSettingsService(
      precautionSettingsRepository);

  @Test
  @DisplayName("유의사항 설정 조회할 수 있다.")
  void getPrecaution() {
    String testShopId = "testShopId";

    String uuid1 = UUIDUtil.shortUUID();
    String uuid2 = UUIDUtil.shortUUID();

    PrecautionSettingsData precautionSettingsData = createPrecautionData(uuid1, uuid2);

    PrecautionSettingsEntity entity = new PrecautionSettingsEntity(testShopId,
        precautionSettingsData);

    PrecautionSettingsEntity savedEntity = precautionSettingsRepository.save(entity);
    PrecautionSettingsEntity targetEntity = service.getPrecautionSettings(testShopId);

    assertEquals(savedEntity, targetEntity);
  }


  @Test
  @DisplayName("저장된 유의사항 설정이 없다면 기본 설정으로 조회할 수 있다.")
  void get_default_OptionSettings() {
    String testShopId = "testShopId";

    PrecautionSettingsEntity defaultEntity = new PrecautionSettingsEntity(testShopId,
        DefaultPrecautionSettingDataFactory.create());

    PrecautionSettingsEntity targetEntity = service.getPrecautionSettings(testShopId);

    assertEquals(defaultEntity, targetEntity);
  }

  @Test
  @DisplayName("유의사항 설정을 저장할 수 있다.")
  void save() {
    String testShopId = "testShopId";

    String uuid1 = UUIDUtil.shortUUID();
    String uuid2 = UUIDUtil.shortUUID();

    PrecautionSettingsData precautionSettingsData = createPrecautionData(uuid1, uuid2);
    PrecautionSettingsEntity targetEntity = new PrecautionSettingsEntity(testShopId,
        precautionSettingsData);

    PrecautionSettingsEntity savedEntity = service.savePrecautionSettings(
        targetEntity);

    assertEquals(targetEntity, savedEntity);
  }

  @Test
  @DisplayName("유의사항 설정을 수정할 수 있다.")
  void save_update() {
    String testShopId = "testShopId";

    String uuid1 = UUIDUtil.shortUUID();
    String uuid2 = UUIDUtil.shortUUID();

    PrecautionSettingsData precautionSettingsData = createPrecautionData(uuid1, uuid2);
    service.savePrecautionSettings(new PrecautionSettingsEntity(testShopId,
        precautionSettingsData));

    String uuid3 = UUIDUtil.shortUUID();
    String uuid4 = UUIDUtil.shortUUID();

    PrecautionSettingsData savedPrecautionSettingsData = createPrecautionData(uuid3, uuid4);
    PrecautionSettingsEntity givenEntity = new PrecautionSettingsEntity(testShopId,
        savedPrecautionSettingsData);
    PrecautionSettingsEntity savedEntity = service.savePrecautionSettings(givenEntity);

    PrecautionSettingsEntity targetEntity = service.getPrecautionSettings(testShopId);

    assertEquals(savedEntity, targetEntity);
  }

  private static PrecautionSettingsData createPrecautionData(String uuid1, String uuid2) {
    return PrecautionSettingsData.builder()
        .messagePrecaution("테스트")
        .isUsedPrecautions(false)
        .precautions(List.of(Precaution.builder()
            .id(uuid1)
            .content("테스트1")
            .build(), Precaution.builder()
            .id(uuid2)
            .content("테스트2")
            .build())).build();
  }

}