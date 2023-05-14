package co.wadcorp.waiting.api.internal.service.person.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemotePersonOptionResponse {

  private final Long shopId;

  @Getter(value = AccessLevel.PRIVATE)
  @JsonProperty("isUsedPersonOptionSetting")
  private final boolean isUsedPersonOptionSetting;

  private final List<PersonOptionVO> personOptions = new ArrayList<>();

  @Builder
  private RemotePersonOptionResponse(Long shopId, boolean isUsedPersonOptionSetting,
      List<PersonOptionVO> personOptions) {
    this.shopId = shopId;
    this.isUsedPersonOptionSetting = isUsedPersonOptionSetting;
    if (personOptions != null) {
      this.personOptions.addAll(personOptions);
    }
  }

  @Getter
  public static class PersonOptionVO {

    private final String id;
    private final String name;

    @Getter(value = AccessLevel.PRIVATE)
    @JsonProperty("isDisplayed")
    private final boolean isDisplayed;

    @Getter(value = AccessLevel.PRIVATE)
    @JsonProperty("isSeat")
    private final boolean isSeat;

    private final List<AdditionalOptionVO> additionalOptions = new ArrayList<>();

    @Builder
    private PersonOptionVO(String id, String name, boolean isDisplayed, boolean isSeat,
        List<AdditionalOptionVO> additionalOptions) {
      this.id = id;
      this.name = name;
      this.isDisplayed = isDisplayed;
      this.isSeat = isSeat;
      if (additionalOptions != null) {
        this.additionalOptions.addAll(additionalOptions);
      }
    }

  }

  @Getter
  public static class AdditionalOptionVO {

    private final String id;
    private final String name;

    @Getter(value = AccessLevel.PRIVATE)
    @JsonProperty("isDisplayed")
    private final boolean isDisplayed;

    @Builder
    private AdditionalOptionVO(String id, String name, boolean isDisplayed) {
      this.id = id;
      this.name = name;
      this.isDisplayed = isDisplayed;
    }

  }

}
