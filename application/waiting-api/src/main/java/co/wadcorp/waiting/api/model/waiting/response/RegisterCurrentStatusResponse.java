package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterCurrentStatusResponse {


  private final WaitingCurrentStatusVO currentStatus;
  private final ShopOperationInfoVO operationInfo;

  @Builder
  public RegisterCurrentStatusResponse(WaitingCurrentStatusVO currentStatus,
      ShopOperationInfoVO operationInfo) {
    this.currentStatus = currentStatus;
    this.operationInfo = operationInfo;
  }
}
