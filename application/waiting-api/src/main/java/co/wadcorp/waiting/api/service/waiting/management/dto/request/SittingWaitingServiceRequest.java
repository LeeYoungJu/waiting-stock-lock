package co.wadcorp.waiting.api.service.waiting.management.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SittingWaitingServiceRequest {

  private final String shopId;
  private final String waitingId;
  private final LocalDate operationDate;
  private final String deviceId;

  @Builder
  public SittingWaitingServiceRequest(String shopId, String waitingId, LocalDate operationDate,
      String deviceId) {
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.operationDate = operationDate;
    this.deviceId = deviceId;
  }
}
