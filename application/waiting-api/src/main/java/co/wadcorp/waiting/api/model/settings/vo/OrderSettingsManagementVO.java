package co.wadcorp.waiting.api.model.settings.vo;

import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderSettingsManagementVO {

  private final Boolean isPossibleOrder;
  private final long countOfMenusUnderStockThreshold;

  @Builder
  private OrderSettingsManagementVO(Boolean isPossibleOrder, long countOfMenusUnderStockThreshold) {
    this.isPossibleOrder = isPossibleOrder;
    this.countOfMenusUnderStockThreshold = countOfMenusUnderStockThreshold;
  }

  public static OrderSettingsManagementVO toDto(OrderSettingsData orderSettings,
      long countOfMenusUnderStockThreshold) {
    return OrderSettingsManagementVO.builder()
        .isPossibleOrder(orderSettings.isPossibleOrder())
        .countOfMenusUnderStockThreshold(countOfMenusUnderStockThreshold)
        .build();
  }

}
