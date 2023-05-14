package co.wadcorp.waiting.api.model.settings.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationTimeForDayVO {

  private String day;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime operationStartTime;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime operationEndTime;
  private Boolean isClosedDay;

}
