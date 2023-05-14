package co.wadcorp.waiting.api.service.waiting.management.dto.request;

import co.wadcorp.waiting.api.model.waiting.vo.CancelReason;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CancelWaitingServiceRequest {

  private final String shopId;
  private final String waitingId;
  private final CancelReason cancelReason;
  private final LocalDate operationDate;
  private final String deviceId;

  @Builder
  public CancelWaitingServiceRequest(String shopId, String waitingId, CancelReason cancelReason,
      LocalDate operationDate,
      String deviceId) {
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.cancelReason = cancelReason;
    this.operationDate = operationDate;
    this.deviceId = deviceId;
  }
}
