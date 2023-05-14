package co.wadcorp.waiting.api.model.settings.request;

import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptionSettingsRequest {

  private Boolean isUsedPersonOptionSetting;
  private List<PersonOptionSetting> personOptionSettings;

  public OptionSettingsEntity toEntity(String shopId) {
    List<OptionSettingsData.PersonOptionSetting> personOptionSettings1 = convertPersonOptionSettings();

    OptionSettingsData optionSettingsData = OptionSettingsData.of(isUsedPersonOptionSetting, personOptionSettings1);
    return new OptionSettingsEntity(shopId, optionSettingsData);
  }

  private List<OptionSettingsData.PersonOptionSetting> convertPersonOptionSettings() {
    return personOptionSettings.stream()
        .map(item -> {
          List<OptionSettingsData.AdditionalOption> additionalOptions = item.additionalOptions.stream()
              .map(additionalOption -> new OptionSettingsData.AdditionalOption(additionalOption.getId(), additionalOption.name, additionalOption.isDisplayed))
              .toList();

          return new OptionSettingsData
              .PersonOptionSetting(item.id, item.name,
              item.isDisplayed, item.isSeat, item.isDefault, item.canModify, additionalOptions);
        })
        .toList();
  }


  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PersonOptionSetting {
    private String id;
    private String name;
    private Boolean isDisplayed;
    private Boolean isSeat;
    private Boolean isDefault;
    private Boolean canModify;
    private List<AdditionalOption> additionalOptions;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AdditionalOption {
    private String id;
    private String name;
    private Boolean isDisplayed;
  }

}
