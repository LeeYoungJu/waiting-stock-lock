package co.wadcorp.waiting.api.model.settings.request;

import co.wadcorp.waiting.api.model.settings.vo.OperationTimeForDayVO;
import co.wadcorp.waiting.api.model.settings.vo.PauseReasonVO;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDay;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationTimeSettingsRequest {

  private List<OperationTimeForDayVO> operationTimeForDays;
  private Boolean isUsedAutoPause;
  private LocalTime autoPauseStartTime;
  private LocalTime autoPauseEndTime;
  private List<PauseReasonVO> pauseReasons;

  public OperationTimeSettingsEntity toEntity(String shopId) {

    List<OperationTimeForDay> operationTimeForDays = convertOperationTimeSettings();
    AutoPauseSettings autoPauseSettings = convertAutoPauseSettings();

    return OperationTimeSettingsEntity.builder()
        .shopId(shopId)
        .operationTimeSettingsData(OperationTimeSettingsData.builder()
            .autoPauseSettings(autoPauseSettings)
            .isUsedAutoPause(isUsedAutoPause)
            .operationTimeForDays(operationTimeForDays)
            .build())
        .build();
  }

  private AutoPauseSettings convertAutoPauseSettings() {
    return AutoPauseSettings.builder()
        .autoPauseStartTime(autoPauseStartTime)
        .autoPauseEndTime(autoPauseEndTime)
        .pauseReasons(convertPauseReasons())
        .build();
  }

  private List<OperationTimeForDay> convertOperationTimeSettings() {
    return operationTimeForDays.stream()
        .map(e -> OperationTimeForDay.builder()
            .day(e.getDay())
            .operationStartTime(e.getOperationStartTime())
            .operationEndTime(e.getOperationEndTime())
            .isClosedDay(e.getIsClosedDay())
            .build())
        .toList();
  }

  private List<PauseReason> convertPauseReasons() {
    return pauseReasons.stream()
        .map(e -> PauseReason.builder()
            .id(e.getId())
            .isDefault(e.getIsDefault())
            .reason(e.getReason())
            .build())
        .toList();
  }

}
