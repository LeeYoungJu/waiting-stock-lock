package co.wadcorp.waiting.api.model.settings.vo;

import co.wadcorp.waiting.data.domain.settings.Precaution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrecautionVO {

  private String id;
  private String content;

  public static PrecautionVO toDto(Precaution precaution) {
    return PrecautionVO.builder()
        .id(precaution.getId())
        .content(precaution.getContent())
        .build();
  }
}
