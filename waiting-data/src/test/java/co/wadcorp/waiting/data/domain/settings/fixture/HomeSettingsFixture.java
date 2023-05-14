package co.wadcorp.waiting.data.domain.settings.fixture;

import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class HomeSettingsFixture {

  public static HomeSettingsEntity createDefaultHomeSettingsWithRegDateTime(String shopId, ZonedDateTime regDateTime) {
    HomeSettingsEntity homeSettings = createDefaultHomeSettings(shopId);

    ReflectionTestUtils.setField(homeSettings, "regDateTime", regDateTime);
    return homeSettings;
  }

  public static HomeSettingsEntity createDefaultHomeSettings(String shopId) {
    return new HomeSettingsEntity(shopId,
        DefaultHomeSettingDataFactory.create());
  }

}
