package co.wadcorp.waiting.api.model.settings.vo;

import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperationTimeSettingsVO {

  private List<OperationTimeForDayVO> operationTimeForDays;
  private Boolean isUsedAutoPause;

  @JsonFormat(pattern = "HH:mm")  // jacksonConfig 설정이 있으므로 불필요하지만, RestDocs test에서는 적용되지 않아 별도 세팅
  private LocalTime autoPauseStartTime;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime autoPauseEndTime;
  private List<PauseReasonVO> pauseReasons;

  public static OperationTimeSettingsVO toDto(OperationTimeSettingsData settings) {
    return OperationTimeSettingsVO.builder()
        .operationTimeForDays(convertToOperationTimeForDayVOs(settings))
        .isUsedAutoPause(settings.getIsUsedAutoPause())
        .autoPauseStartTime(settings.getAutoPauseStartTime())
        .autoPauseEndTime(settings.getAutoPauseEndTime())
        .pauseReasons(getPauseReasonVOs(settings))
        .build();
  }

  private static List<OperationTimeForDayVO> convertToOperationTimeForDayVOs(OperationTimeSettingsData settings) {
    return settings.getOperationTimeForDays().stream()
        .map(e -> OperationTimeForDayVO.builder()
            .day(e.getDay())
            .operationStartTime(e.getOperationStartTime())
            .operationEndTime(e.getOperationEndTime())
            .isClosedDay(e.getIsClosedDay())
            .build())
        .toList();
  }

  private static List<PauseReasonVO> getPauseReasonVOs(OperationTimeSettingsData settings) {
    return settings.getPauseReasons().stream()
        .map(e -> PauseReasonVO.builder()
            .id(e.getId())
            .isDefault(e.getIsDefault())
            .reason(e.getReason())
            .build())
        .toList();
  }
}
