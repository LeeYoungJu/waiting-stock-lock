package co.wadcorp.waiting.data.domain.message;

import lombok.Getter;

@Getter
public class MessageTemplate {

  private final String templateName;
  private final String templateCode;
  private final String templateContent;
  private final String resendContent;


  public MessageTemplate(String templateName, String templateCode, String templateContent, String resendContent) {
    this.templateName = templateName;
    this.templateCode = templateCode;
    this.templateContent = templateContent;
    this.resendContent = resendContent;
  }
}
