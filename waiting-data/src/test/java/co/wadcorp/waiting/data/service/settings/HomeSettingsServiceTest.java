package co.wadcorp.waiting.data.service.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.FakeHomeSettingsRepository;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import org.junit.jupiter.api.Test;

class HomeSettingsServiceTest {

  private FakeHomeSettingsRepository homeSettingsRepository = new FakeHomeSettingsRepository();

  private HomeSettingsService service = new HomeSettingsService(homeSettingsRepository);

  @Test
  void getHomeSettings() {
    String testShopId = "testShopId";

    HomeSettingsEntity save = homeSettingsRepository.save(createDefaultHomeSettings(testShopId));
    HomeSettingsEntity target = service.getHomeSettings(testShopId);

    assertEquals(save, target);
  }


  @Test
  void saveHomeSettings() {
    String testShopId = "testShopId";

    service.saveHomeSettings(createDefaultHomeSettings(testShopId));
  }

  HomeSettingsEntity createDefaultHomeSettings(String shopId) {
    return HomeSettingsEntity.builder()
        .shopId(shopId)
        .homeSettingsData(DefaultHomeSettingDataFactory.create())
        .build();

  }
}