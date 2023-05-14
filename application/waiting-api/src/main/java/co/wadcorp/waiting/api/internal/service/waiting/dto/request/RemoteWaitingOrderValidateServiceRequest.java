package co.wadcorp.waiting.api.internal.service.waiting.dto.request;

import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteWaitingOrderValidateServiceRequest {

  private RemoteOrderDto order;

  @Builder
  private RemoteWaitingOrderValidateServiceRequest(RemoteOrderDto order) {
    this.order = order;
  }

  public List<String> getMenuIds() {
    return this.order.getMenuIds();
  }

  public List<MenuQuantity> toMenuQuantity() {
    return this.order.toMenuQuantity();
  }
}
