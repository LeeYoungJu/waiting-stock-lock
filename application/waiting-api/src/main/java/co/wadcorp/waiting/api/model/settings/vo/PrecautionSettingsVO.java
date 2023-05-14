package co.wadcorp.waiting.api.model.settings.vo;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.stream.StreamUtils;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsData;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class PrecautionSettingsVO {

  private final String messagePrecaution;
  private final Boolean isUsedPrecautions;
  private final List<PrecautionVO> precautions;

  @Builder
  public PrecautionSettingsVO(String messagePrecaution, Boolean isUsedPrecautions,
      List<PrecautionVO> precautions) {
    this.messagePrecaution = messagePrecaution;
    this.isUsedPrecautions = isUsedPrecautions;
    this.precautions = precautions;
  }

  public static PrecautionSettingsVO toDto(PrecautionSettingsData precautionSettingsData) {
    return PrecautionSettingsVO.builder()
        .messagePrecaution(precautionSettingsData.getMessagePrecaution())
        .isUsedPrecautions(precautionSettingsData.getIsUsedPrecautions())
        .precautions(toPrecautionVO(precautionSettingsData.getPrecautions()))
        .build();
  }


  private static List<PrecautionVO> toPrecautionVO(List<Precaution> precautions) {
    return convert(precautions, PrecautionVO::toDto);
  }
}
