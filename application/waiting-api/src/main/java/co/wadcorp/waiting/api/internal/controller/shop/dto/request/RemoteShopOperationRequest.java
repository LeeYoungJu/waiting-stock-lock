package co.wadcorp.waiting.api.internal.controller.shop.dto.request;

import co.wadcorp.waiting.api.internal.service.shop.dto.request.RemoteShopOperationServiceRequest;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteShopOperationRequest {

  @NotNull(message = "운영일은 필수입니다.")
  private LocalDate operationDate;

  @Builder
  private RemoteShopOperationRequest(LocalDate operationDate) {
    this.operationDate = operationDate;
  }

  public RemoteShopOperationServiceRequest toServiceRequest() {
    return RemoteShopOperationServiceRequest.builder()
        .operationDate(operationDate)
        .build();
  }

}
