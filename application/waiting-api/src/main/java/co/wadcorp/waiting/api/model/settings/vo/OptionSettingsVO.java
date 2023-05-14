package co.wadcorp.waiting.api.model.settings.vo;

import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OptionSettingsVO {

  private Boolean isUsedPersonOptionSetting;
  private List<PersonOptionSetting> personOptionSettings;

  @Getter
  @Builder
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
  @Builder
  @AllArgsConstructor
  public static class AdditionalOption {
    private String id;
    private String name;
    private Boolean isDisplayed;
  }

  public static OptionSettingsVO toDto(OptionSettingsData optionSettingsData) {

    List<PersonOptionSetting> personOptionSettings = getPersonOptionSettings(optionSettingsData);

    return new OptionSettingsVO(optionSettingsData.getIsUsedPersonOptionSetting(), personOptionSettings);
  }


  private static List<PersonOptionSetting> getPersonOptionSettings(OptionSettingsData data) {
    return data.getPersonOptionSettings()
        .stream()
        .map(item -> {
          List<AdditionalOption> additionalOptions = item.getAdditionalOptions().stream()
              .map(additionalOption ->
                  new AdditionalOption(additionalOption.getId(), additionalOption.getName(), additionalOption.getIsDisplayed()))
              .toList();

          return new PersonOptionSetting(item.getId(), item.getName(), item.getIsDisplayed(),
              item.getIsSeat(), item.getIsDefault(), item.getCanModify(), additionalOptions);
        })
        .toList();
  }
}
