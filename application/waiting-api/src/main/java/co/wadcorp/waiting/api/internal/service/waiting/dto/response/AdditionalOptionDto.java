package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdditionalOptionDto {

  private final String name;
  private final int count;

  @Builder
  private AdditionalOptionDto(String name, int count) {
    this.name = name;
    this.count = count;
  }

}
