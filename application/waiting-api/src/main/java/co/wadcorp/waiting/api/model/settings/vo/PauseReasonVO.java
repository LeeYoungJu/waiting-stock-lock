package co.wadcorp.waiting.api.model.settings.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PauseReasonVO {
  private String id;
  private Boolean isDefault;
  private String reason;
}
