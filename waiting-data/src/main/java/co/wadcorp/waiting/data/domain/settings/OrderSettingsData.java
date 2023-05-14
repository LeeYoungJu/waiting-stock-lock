package co.wadcorp.waiting.data.domain.settings;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSettingsData {

  private boolean isPossibleOrder;

  public static OrderSettingsData of(Boolean isPossibleOrder) {
    OrderSettingsData result = new OrderSettingsData();
    result.isPossibleOrder = isPossibleOrder;
    return result;
  }

  @Builder
  private OrderSettingsData(boolean isPossibleOrder) {
    this.isPossibleOrder = isPossibleOrder;
  }

}