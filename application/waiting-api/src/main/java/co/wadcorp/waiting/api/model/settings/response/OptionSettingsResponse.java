package co.wadcorp.waiting.api.model.settings.response;

import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptionSettingsResponse {

  @JsonUnwrapped
  private OptionSettingsVO optionSettings;

  private Boolean existsWaitingTeam;  // 웨이팅 중인 팀이 존재하면 설정을 수정할 수 없다.
  private Boolean isOpenedOperation;  // 웨이팅 접수 중인경우 설정을 수정할 수 없다.

  public static OptionSettingsResponse toDto(OptionSettingsEntity entity, Boolean existsWaitingTeam, Boolean isOpenedOperation) {
    return new OptionSettingsResponse(OptionSettingsVO.toDto(entity.getOptionSettingsData()), existsWaitingTeam, isOpenedOperation);
  }
}
