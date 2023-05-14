package co.wadcorp.waiting.data.query.message.dto;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.message.SendChannel;
import co.wadcorp.waiting.data.domain.message.SendStatus;
import co.wadcorp.waiting.data.domain.message.SendType;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class MessageSendHistoryDto {

  private Long seq;
  private Long waitingHistorySeq;
  private String waitingId;
  private String requestId;
  private PhoneNumber encCustomerPhone;
  private SendChannel sendChannel;
  private String templateName;
  private SendType sendType;
  private String templateCode;
  private String content;
  private String buttons;
  private SendStatus status;
  private String failCode;
  private String failReason;
  private ZonedDateTime sendDateTime;

  public MessageSendHistoryDto() {
  }
}
