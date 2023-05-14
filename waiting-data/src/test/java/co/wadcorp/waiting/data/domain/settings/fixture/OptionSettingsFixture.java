package co.wadcorp.waiting.data.domain.settings.fixture;

import co.wadcorp.waiting.data.domain.settings.DefaultOptionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class OptionSettingsFixture {

  public static OptionSettingsEntity createDefaultOptionSettingsWithRegDateTime(String shopId,
      ZonedDateTime regDateTime) {
    OptionSettingsEntity optionSettings = createDefaultOptionSettings(shopId);

    ReflectionTestUtils.setField(optionSettings, "regDateTime", regDateTime);
    return optionSettings;
  }

  public static OptionSettingsEntity createDefaultOptionSettings(String shopId) {
    return new OptionSettingsEntity(shopId,
        DefaultOptionSettingDataFactory.create());
  }

}
