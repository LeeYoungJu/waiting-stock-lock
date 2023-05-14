package co.wadcorp.waiting.api.service.settings.dto.request;


import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderSettingsServiceRequest {

  private boolean isPossibleOrder;

  @Builder
  private OrderSettingsServiceRequest(boolean isPossibleOrder) {
    this.isPossibleOrder = isPossibleOrder;
  }

  public OrderSettingsEntity toEntity(String shopId) {
    return OrderSettingsEntity.builder()
        .shopId(shopId)
        .orderSettingsData(OrderSettingsData.builder()
            .isPossibleOrder(isPossibleOrder)
            .build()
        )
        .build();
  }

}
