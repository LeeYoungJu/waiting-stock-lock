package co.wadcorp.waiting.api.model.settings.response;

import co.wadcorp.waiting.api.model.settings.vo.HomeSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OptionSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.OrderSettingsVO;
import co.wadcorp.waiting.api.model.settings.vo.PrecautionSettingsVO;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsData;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterSettingsResponse {

  private final OptionSettingsVO optionSettings;
  private final HomeSettingsVO homeSettings;
  private final PrecautionSettingsVO precautionSettings;
  private final OrderSettingsVO orderSettings;

  public static RegisterSettingsResponse toDto(HomeSettingsData homeSettingsData,
      OptionSettingsData optionSettingsData, PrecautionSettingsData precautionSettingsData,
      OrderSettingsData orderSettings) {

    return RegisterSettingsResponse.builder()
        .homeSettings(HomeSettingsVO.toDto(homeSettingsData))
        .optionSettings(OptionSettingsVO.toDto(optionSettingsData))
        .precautionSettings(PrecautionSettingsVO.toDto(precautionSettingsData))
        .orderSettings(OrderSettingsVO.toDto(orderSettings))
        .build();
  }
}
