package co.wadcorp.waiting.api.model.waiting.vo;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebPersonOptionVO {

  private String name;
  private Integer count;
  private List<AdditionalOption> additionalOptions;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AdditionalOption {
    private String name;
    private Integer count;

    public AdditionalOption(PersonOption.AdditionalOption additionalOption) {
      this.name = additionalOption.getName();
      this.count = additionalOption.getCount();
    }
  }

  public WebPersonOptionVO(PersonOption personOption) {
    this.name = personOption.getName();
    this.count = personOption.getCount();
    this.additionalOptions = convertToAdditionalOptions(personOption.getAdditionalOptions());
  }

  private List<AdditionalOption> convertToAdditionalOptions(List<PersonOption.AdditionalOption> additionalOptions) {
    return convert(additionalOptions, AdditionalOption::new);
  }
}
