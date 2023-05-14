package co.wadcorp.waiting.api.model.settings.vo;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.stream.StreamUtils;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeSettingsVO {

  private String waitingModeType;

  private SeatOptionSettingVO defaultModeSettings;
  private List<SeatOptionSettingVO> tableModeSettings;

  public static HomeSettingsVO toDto(HomeSettingsData homeSettingsData) {
    SeatOptionSettingVO defaultModeSettings = toDefaultModeSettingVO(homeSettingsData.getDefaultModeSettings());
    List<SeatOptionSettingVO> tableModeSettings = toTableModeSettingVO(homeSettingsData.getTableModeSettings());

    return HomeSettingsVO.builder()
        .waitingModeType(homeSettingsData.getWaitingModeType())
        .defaultModeSettings(defaultModeSettings)
        .tableModeSettings(tableModeSettings)
        .build();
  }

  private static SeatOptionSettingVO toDefaultModeSettingVO(SeatOptions defaultModeSetting) {
    return SeatOptionSettingVO.toDto(defaultModeSetting);
  }

  private static List<SeatOptionSettingVO> toTableModeSettingVO(List<SeatOptions> tableModeSettings) {
    return convert(tableModeSettings, SeatOptionSettingVO::toDto);
  }

}
