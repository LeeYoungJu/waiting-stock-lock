package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.exception.AppException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode
public class OptionSettingsData {

  private Boolean isUsedPersonOptionSetting;
  private List<PersonOptionSetting> personOptionSettings;

  private OptionSettingsData() {
  }

  public static OptionSettingsData of(Boolean isUsedPersonOptionSetting,
      List<PersonOptionSetting> personOptionSettings) {

    OptionSettingsData result = new OptionSettingsData();

    result.isUsedPersonOptionSetting = isUsedPersonOptionSetting;
    result.personOptionSettings = personOptionSettings;

    return result;
  }

  public PersonOptionSetting findPersonOption(String id) {
    return this.personOptionSettings.stream()
        .filter(item -> id.equals(item.getId()))
        .findFirst()
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "인원 정보를 찾지 못했습니다."));
  }

  @Getter
  @EqualsAndHashCode
  @NoArgsConstructor
  public static class PersonOptionSetting {

    private String id;
    private String name;
    private Boolean isDisplayed;
    private Boolean isSeat;
    private Boolean isDefault;
    private Boolean canModify;
    private List<AdditionalOption> additionalOptions;

    @Builder
    public PersonOptionSetting(String id, String name, Boolean isDisplayed,
        Boolean isSeat,
        Boolean isDefault, Boolean canModify, List<AdditionalOption> additionalOptions) {
      this.id = id;
      this.name = name;
      this.isDisplayed = isDisplayed;
      this.isSeat = isSeat;
      this.isDefault = isDefault;
      this.canModify = canModify;
      this.additionalOptions = Objects.requireNonNullElseGet(additionalOptions, ArrayList::new);
    }

    public AdditionalOption findAdditionalOption(String id) {
      return this.additionalOptions.stream()
          .filter(item -> item.getId().equals(id))
          .findFirst()
          .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,  "추가 옵션 정보를 찾지 못했습니다."));
    }
  }

  @Getter
  @EqualsAndHashCode
  @NoArgsConstructor
  public static class AdditionalOption {
    private String id;
    private String name;
    private Boolean isDisplayed;

    @Builder
    public AdditionalOption(String id, String name, Boolean isDisplayed) {
      this.id = id;
      this.name = name;
      this.isDisplayed = isDisplayed;
    }
  }

  public boolean isNotUsePersonOptionSetting() {
    return !this.isUsedPersonOptionSetting;
  }
}