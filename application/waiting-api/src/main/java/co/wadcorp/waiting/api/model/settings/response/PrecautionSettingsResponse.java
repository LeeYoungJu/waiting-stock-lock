package co.wadcorp.waiting.api.model.settings.response;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.stream.StreamUtils;
import co.wadcorp.waiting.api.model.settings.vo.PrecautionVO;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsData;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrecautionSettingsResponse {

  private final String messagePrecaution;
  private final Boolean isUsedPrecautions;
  private final List<PrecautionVO> precautions;

  @Builder
  public PrecautionSettingsResponse(String messagePrecaution, Boolean isUsedPrecautions,
      List<PrecautionVO> precautions) {
    this.messagePrecaution = messagePrecaution;
    this.isUsedPrecautions = isUsedPrecautions;
    this.precautions = precautions;
  }

  public static PrecautionSettingsResponse toDto(PrecautionSettingsEntity entity) {
    PrecautionSettingsData precautionSettingsData = entity.getPrecautionSettingsData();
    return PrecautionSettingsResponse.builder()
        .messagePrecaution(precautionSettingsData.getMessagePrecaution())
        .isUsedPrecautions(precautionSettingsData.getIsUsedPrecautions())
        .precautions(toPrecautionVO(precautionSettingsData.getPrecautions()))
        .build();
  }


  private static List<PrecautionVO> toPrecautionVO(List<Precaution> precautions) {
    return convert(precautions, PrecautionVO::toDto);
  }
}
