package co.wadcorp.waiting.api.service.waiting.management.dto.request;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CallWaitingServiceRequest {

  private final String shopId;
  private final String waitingId;
  private final LocalDate operationDate;
  private final ZonedDateTime currentDateTime;
  private final String deviceId;

  @Builder
  public CallWaitingServiceRequest(String shopId, String waitingId, LocalDate operationDate,
      ZonedDateTime currentDateTime, String deviceId) {
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.operationDate = operationDate;
    this.currentDateTime = currentDateTime;
    this.deviceId = deviceId;
  }
}
