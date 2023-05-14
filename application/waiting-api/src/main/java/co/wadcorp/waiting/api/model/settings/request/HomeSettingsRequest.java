package co.wadcorp.waiting.api.model.settings.request;

import co.wadcorp.waiting.api.model.settings.vo.SeatOptionSettingVO;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeSettingsRequest {

  private String waitingModeType;

  private SeatOptionSettingVO defaultModeSettings;
  private List<SeatOptionSettingVO> tableModeSettings;

  public HomeSettingsEntity toEntity(String shopId) {
    return HomeSettingsEntity.builder()
        .shopId(shopId)
        .homeSettingsData(convertHomeSettingsData())
        .build();
  }

  private HomeSettingsData convertHomeSettingsData() {
    return HomeSettingsData.builder()
        .waitingModeType(WaitingModeType.getValue(waitingModeType))
        .defaultModeSettings(convertDefaultModeSettings())
        .tableModeSettings(convertTableModeSettings())
        .build();
  }

  private SeatOptions convertDefaultModeSettings() {
    Boolean isTakeOut = defaultModeSettings.getIsPickup();

    return SeatOptions.builder()
        .id(defaultModeSettings.getId())
        .name(isTakeOut
            ? SeatOptions.DEFAULT_MODE_TAKE_OUT_NAME_TEXT
            : SeatOptions.DEFAULT_MODE_NOT_TAKE_OUT_NAME_TEXT
        )
        .minSeatCount(defaultModeSettings.getMinSeatCount())
        .maxSeatCount(defaultModeSettings.getMaxSeatCount())
        .expectedWaitingPeriod(defaultModeSettings.getExpectedWaitingPeriod())
        .isUsedExpectedWaitingPeriod(defaultModeSettings.getIsUsedExpectedWaitingPeriod())
        .isDefault(defaultModeSettings.getIsDefault())
        .isTakeOut(isTakeOut)
        .build();
  }

  private List<SeatOptions> convertTableModeSettings() {
    return tableModeSettings.stream()
        .map(e -> SeatOptions.builder()
            .id(e.getId())
            .name(e.getName())
            .minSeatCount(e.getMinSeatCount())
            .maxSeatCount(e.getMaxSeatCount())
            .expectedWaitingPeriod(e.getExpectedWaitingPeriod())
            .isUsedExpectedWaitingPeriod(e.getIsUsedExpectedWaitingPeriod())
            .isDefault(e.getIsDefault())
            .isTakeOut(e.getIsPickup())
            .build())
        .toList();
  }

}
