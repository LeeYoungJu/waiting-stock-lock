package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TableDto {

  private final String id;
  private final String name;
  private final Boolean isTakeOut;

  @Builder
  private TableDto(String id, String name, Boolean isTakeOut) {
    this.id = id;
    this.name = name;
    this.isTakeOut = isTakeOut;
  }
}
