package co.wadcorp.waiting.api.internal.service.table.dto.response;

import co.wadcorp.waiting.data.enums.WaitingModeType;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteTableSettingResponse {

  private final Long shopId;
  private final WaitingModeType waitingModeType;
  private final ModeSettingsVO defaultModeSettings;
  private final List<ModeSettingsVO> tableModeSettings = new ArrayList<>();

  @Builder
  private RemoteTableSettingResponse(Long shopId, WaitingModeType waitingModeType,
      ModeSettingsVO defaultModeSettings, List<ModeSettingsVO> tableModeSettings) {
    this.shopId = shopId;
    this.waitingModeType = waitingModeType;
    this.defaultModeSettings = defaultModeSettings;
    if (tableModeSettings != null) {
      this.tableModeSettings.addAll(tableModeSettings);
    }
  }

  @Getter
  public static class ModeSettingsVO {

    private final String id;
    private final String name;
    private final int minSeatCount;
    private final int maxSeatCount;
    private final int expectedWaitingPeriod;

    @Getter(value = AccessLevel.PRIVATE)
    @JsonProperty("isUsedExpectedWaitingPeriod")
    private boolean isUsedExpectedWaitingPeriod;

    @Getter(value = AccessLevel.PRIVATE)
    @JsonProperty("isTakeOut")
    private boolean isTakeOut;

    @Builder
    private ModeSettingsVO(String id, String name, int minSeatCount, int maxSeatCount,
        int expectedWaitingPeriod, boolean isUsedExpectedWaitingPeriod, boolean isTakeOut) {
      this.id = id;
      this.name = name;
      this.minSeatCount = minSeatCount;
      this.maxSeatCount = maxSeatCount;
      this.expectedWaitingPeriod = expectedWaitingPeriod;
      this.isUsedExpectedWaitingPeriod = isUsedExpectedWaitingPeriod;
      this.isTakeOut = isTakeOut;
    }

  }

}
