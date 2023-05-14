package co.wadcorp.waiting.api.model.waiting.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingRegisterResponse {

  private final String waitingId;
  private final Integer waitingNumber;

  @Builder
  public WaitingRegisterResponse(String waitingId, Integer waitingNumber) {
    this.waitingId = waitingId;
    this.waitingNumber = waitingNumber;
  }
}
