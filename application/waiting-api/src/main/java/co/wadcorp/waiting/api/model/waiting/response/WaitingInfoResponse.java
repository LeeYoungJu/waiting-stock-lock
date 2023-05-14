package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.waiting.api.model.waiting.vo.PageVO;
import co.wadcorp.waiting.api.model.waiting.vo.ShopOperationInfoVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingCurrentStatusVO;
import co.wadcorp.waiting.api.model.waiting.vo.WaitingVO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingInfoResponse {

  private final WaitingCurrentStatusVO currentStatus;
  private final ShopOperationInfoVO operationInfo;
  private final List<WaitingVO> waiting;

  @JsonUnwrapped
  private final PageVO page;

  @Builder
  public WaitingInfoResponse(WaitingCurrentStatusVO currentStatus,
      ShopOperationInfoVO operationInfo, PageVO page,
      List<WaitingVO> waiting) {
    this.currentStatus = currentStatus;
    this.operationInfo = operationInfo;
    this.page = page;
    this.waiting = waiting;
  }
}
