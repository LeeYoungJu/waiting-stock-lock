package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PersonOptionDto {

  private final String name;
  private final int count;
  private final List<AdditionalOptionDto> additionalOptions;

  @Builder
  private PersonOptionDto(String name, int count, List<AdditionalOptionDto> additionalOptions) {
    this.name = name;
    this.count = count;
    this.additionalOptions = additionalOptions;
  }

}
