package co.wadcorp.waiting.api.model.settings.response;

import co.wadcorp.waiting.api.model.settings.vo.HomeSettingsVO;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeSettingsResponse {

  @JsonUnwrapped
  private HomeSettingsVO homeSettings;

  private Boolean existsWaitingTeam;  // 웨이팅 중인 팀이 존재하면 설정을 수정할 수 없다.
  private Boolean isOpenedOperation;  // 웨이팅 접수 중인경우 설정을 수정할 수 없다.

  public static HomeSettingsResponse toDto(HomeSettingsData settings, Boolean existsWaitingTeam,
      Boolean isOpenedOperation) {
    return HomeSettingsResponse.builder()
        .homeSettings(HomeSettingsVO.toDto(settings))
        .existsWaitingTeam(existsWaitingTeam)
        .isOpenedOperation(isOpenedOperation)
        .build();
  }
}
