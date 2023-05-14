package co.wadcorp.waiting.api.model.settings.vo;

import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatOptionSettingVO {

  private String id;
  private String name;
  private Integer minSeatCount;
  private Integer maxSeatCount;
  private Integer expectedWaitingPeriod;
  private Boolean isUsedExpectedWaitingPeriod;
  private Boolean isDefault;
  private Boolean isPickup; // TODO: 2023/03/09 front 협의 후 naming 변경

  public static SeatOptionSettingVO toDto(SeatOptions seatOptions) {
    return SeatOptionSettingVO.builder()
        .id(seatOptions.getId())
        .name(seatOptions.getName())
        .minSeatCount(seatOptions.getMinSeatCount())
        .maxSeatCount(seatOptions.getMaxSeatCount())
        .expectedWaitingPeriod(seatOptions.getExpectedWaitingPeriod())
        .isUsedExpectedWaitingPeriod(seatOptions.getIsUsedExpectedWaitingPeriod())
        .isDefault(seatOptions.getIsDefault())
        .isPickup(seatOptions.getIsTakeOut())
        .build();
  }
}
