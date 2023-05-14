package co.wadcorp.waiting.api.internal.service.shop.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteShopOperationServiceRequest {

  private final LocalDate operationDate;

  @Builder
  private RemoteShopOperationServiceRequest(LocalDate operationDate) {
    this.operationDate = operationDate;
  }

}
