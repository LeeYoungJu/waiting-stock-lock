package co.wadcorp.waiting.infra.message.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SendMessageResponse {

  private final String requestId;
  private final Integer resultCode;
  private final String resultMessage;

  @Builder
  public SendMessageResponse(String requestId, Integer resultCode, String resultMessage) {
    this.requestId = requestId;
    this.resultCode = resultCode;
    this.resultMessage = resultMessage;
  }
}
