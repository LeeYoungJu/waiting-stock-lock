package co.wadcorp.waiting.infra.message.dto;

import co.wadcorp.libs.phone.PhoneNumber;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SendMessageRequest {

  private final PhoneNumber phoneNumber;
  private final String templateCode;
  private final String templateContent;
  private final Map<String, String> templateParameter;
  private final String resendContent;

  @Builder
  public SendMessageRequest(PhoneNumber phoneNumber, String templateCode, String templateContent,
      Map<String, String> templateParameter, String resendContent) {
    this.phoneNumber = phoneNumber;
    this.templateCode = templateCode;
    this.templateContent = templateContent;
    this.templateParameter = templateParameter;
    this.resendContent = resendContent;
  }
}
