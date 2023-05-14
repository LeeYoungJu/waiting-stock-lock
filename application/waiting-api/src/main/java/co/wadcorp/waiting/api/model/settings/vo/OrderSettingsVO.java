package co.wadcorp.waiting.api.model.settings.vo;

import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import lombok.Getter;

@Getter
public class OrderSettingsVO {

  private Boolean isPossibleOrder;

  public static OrderSettingsVO toDto(OrderSettingsData orderSettings) {

    OrderSettingsVO orderSettingsVO = new OrderSettingsVO();
    orderSettingsVO.isPossibleOrder = orderSettings.isPossibleOrder();
    return orderSettingsVO;
  }

}
