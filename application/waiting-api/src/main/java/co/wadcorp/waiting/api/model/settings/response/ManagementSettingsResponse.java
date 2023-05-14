package co.wadcorp.waiting.api.model.settings.response;

import co.wadcorp.waiting.api.model.settings.vo.AlarmSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.HomeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OperationTimeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OrderSettingsManagementVO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagementSettingsResponse {

  private final OptionSettingsVO optionSettings;
  private final HomeSettingsVO homeSettings;
  private final AlarmSettingsVO alarmSettings;
  private final OperationTimeSettingsVO operationTimeSettings;
  private final OrderSettingsManagementVO orderSettings;

}
