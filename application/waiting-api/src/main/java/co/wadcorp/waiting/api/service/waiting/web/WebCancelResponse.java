package co.wadcorp.waiting.api.service.waiting.web;

import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WebCancelResponse {

  private final String waitingId;
  private final String shopName;
  private final String shopAddress;
  private final String shopTelNumber;
  private final WaitingStatus waitingStatus;
  private final WaitingDetailStatus waitingDetailStatus;


  @Builder
  public WebCancelResponse(String waitingId, String shopName, String shopAddress,
      String shopTelNumber, WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus) {
    this.waitingId = waitingId;
    this.shopName = shopName;
    this.shopAddress = shopAddress;
    this.shopTelNumber = shopTelNumber;
    this.waitingStatus = waitingStatus;
    this.waitingDetailStatus = waitingDetailStatus;
  }
}
