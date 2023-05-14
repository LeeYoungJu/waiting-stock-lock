package co.wadcorp.waiting.api.service.waiting.management.dto.response;

import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ManagementWaitingCurrentStatusResponse {

  private final WaitingCurrentStatusVO currentStatus;
  private final ShopOperationInfoVO operationInfo;

  @Builder
  public ManagementWaitingCurrentStatusResponse(WaitingCurrentStatusVO currentStatus,
      ShopOperationInfoVO operationInfo) {
    this.currentStatus = currentStatus;
    this.operationInfo = operationInfo;
  }
}
