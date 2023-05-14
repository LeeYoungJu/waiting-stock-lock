package co.wadcorp.waiting.api.model.settings.request;

import co.wadcorp.waiting.api.model.settings.vo.PrecautionVO;
import co.wadcorp.waiting.api.model.settings.vo.SeatOptionSettingVO;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsData;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrecautionSettingsRequest {

  private String messagePrecaution;
  private Boolean isUsedPrecautions;
  private List<PrecautionVO> precautions;

  public PrecautionSettingsEntity toEntity(String shopId) {
    return PrecautionSettingsEntity.builder()
        .shopId(shopId)
        .precautionSettingsData(convertPrecautionSettingsData())
        .build();
  }

  private PrecautionSettingsData convertPrecautionSettingsData() {
    return PrecautionSettingsData.builder()
        .messagePrecaution(messagePrecaution)
        .isUsedPrecautions(isUsedPrecautions)
        .precautions(convertPrecautions())
        .build();
  }

  private List<Precaution> convertPrecautions() {
    return precautions.stream()
        .map(e -> Precaution.builder()
            .id(e.getId())
            .content(e.getContent())
            .build())
        .collect(Collectors.toList());
  }
}
