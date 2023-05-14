package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.data.exception.AppException;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class HomeSettingsData {

  private String waitingModeType;
  private SeatOptions defaultModeSettings;
  private List<SeatOptions> tableModeSettings;

  @Builder
  public HomeSettingsData(String waitingModeType,
      SeatOptions defaultModeSettings, List<SeatOptions> tableModeSettings) {
    HomeSettingsDataValidator.validateTableModeSeatOptions(tableModeSettings);

    this.waitingModeType = waitingModeType;
    this.defaultModeSettings = defaultModeSettings;
    this.tableModeSettings = tableModeSettings;
  }

  public Integer getExpectedPeriodBySeatOption(String seatOptionName) {
    SeatOptions seatOptionsByMode = this.findSeatOptionsBySeatOptionName(seatOptionName);
    if (seatOptionsByMode.isNotUseExpectedWaitingPeriod()) {
      return null;
    }

    return seatOptionsByMode.getExpectedWaitingPeriod();
  }

  public SeatOptions findSeatOptionsBySeatOptionId(String seatOptionId) {
    if (isDefaultMode()) {
      return this.defaultModeSettings;
    }
    return findTableModeSettingBySeatOptionId(seatOptionId);
  }

  public SeatOptions findSeatOptionsBySeatOptionName(String seatOptionName) {
    if (isDefaultMode()) {
      return this.defaultModeSettings;
    }
    return findTableModeSettingBySeatOptionName(seatOptionName);
  }

  public boolean isDefaultMode() {
    return WaitingModeType.DEFAULT == WaitingModeType.valueOf(this.waitingModeType);
  }

  private SeatOptions findTableModeSettingBySeatOptionId(String seatOptionId) {
    return tableModeSettings.stream()
        .filter(item -> StringUtils.equals(item.getId(), seatOptionId))
        .findFirst()
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "좌석을 검색하지 못했습니다."));
  }

  private SeatOptions findTableModeSettingBySeatOptionName(String seatOptionName) {
    return tableModeSettings.stream()
        .filter(item -> StringUtils.equals(item.getName(), seatOptionName))
        .findFirst()
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "좌석을 검색하지 못했습니다."));
  }

}
