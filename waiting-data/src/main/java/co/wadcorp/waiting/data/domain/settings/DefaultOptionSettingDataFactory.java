package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.AdditionalOption;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData.PersonOptionSetting;
import java.util.List;

public class DefaultOptionSettingDataFactory {

  public static final boolean DEFAULT_PERSON_OPTION_SETTING_USE_YN = false;
  private static final String ADULT_UUID = "qmtq_wB7SD-S543AapN3zw";
  private static final String CHILD_UUID = "ry91rSXCTJewVYYz4ZCbfg";
  private static final String ELEMENTARY_STUDENT_UUID = "dqcJbFkrQoOjkZmEYTCttA";
  private static final String COMPANION_ANIMAL_UUID = "TvH8L3q4T5eaHck1ubC6ng";
  private static final String CHILD_CHAIR_UUID = "F7pbspRoRfSBrZh6VhcO0A";

  public static OptionSettingsData create() {
    return OptionSettingsData.of(DEFAULT_PERSON_OPTION_SETTING_USE_YN, createDefaultPersonOptionsSettings());
  }

  private static List<PersonOptionSetting> createDefaultPersonOptionsSettings() {
    return List.of(createAdultPersonOptionsSetting(),
        createChildPersonOptionsSetting(),
        createElementaryStudentPersonOptionsSetting(),
        createCompanionAnimalPersonOptionsSetting());
  }

  private static PersonOptionSetting createAdultPersonOptionsSetting() {
    return PersonOptionSetting.builder()
        .id(ADULT_UUID)
        .name("성인")
        .canModify(false)
        .isDefault(true)
        .isDisplayed(true)
        .isSeat(true)
        .build();
  }

  private static PersonOptionSetting createChildPersonOptionsSetting() {
    return PersonOptionSetting.builder()
        .id(CHILD_UUID)
        .name("유아")
        .canModify(true)
        .isDefault(true)
        .isDisplayed(true)
        .isSeat(true)
        .additionalOptions(List.of(AdditionalOption.builder()
            .name("유아용 의자")
            .id(CHILD_CHAIR_UUID)
            .isDisplayed(true)
            .build()))
        .build();
  }

  private static PersonOptionSetting createElementaryStudentPersonOptionsSetting() {
    return PersonOptionSetting.builder()
        .id(ELEMENTARY_STUDENT_UUID)
        .name("초등")
        .canModify(true)
        .isDefault(false)
        .isDisplayed(false)
        .isSeat(false)
        .build();
  }

  private static PersonOptionSetting createCompanionAnimalPersonOptionsSetting() {
    return PersonOptionSetting.builder()
        .id(COMPANION_ANIMAL_UUID)
        .name("반려동물")
        .canModify(true)
        .isDefault(false)
        .isDisplayed(false)
        .isSeat(false)
        .build();
  }
}
