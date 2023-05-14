package co.wadcorp.waiting.api.model.waiting.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingRegisterByManagerResponse {

  private final String waitingId;
  private final Integer waitingNumber;

  @Builder
  public WaitingRegisterByManagerResponse(String waitingId, Integer waitingNumber) {
    this.waitingId = waitingId;
    this.waitingNumber = waitingNumber;
  }
}
