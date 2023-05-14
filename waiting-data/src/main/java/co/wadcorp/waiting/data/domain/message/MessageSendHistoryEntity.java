package co.wadcorp.waiting.data.domain.message;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.PhoneNumberConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cw_message_send_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageSendHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "waiting_history_seq")
  private Long waitingHistorySeq;

  @Column(name = "waiting_id")
  private String waitingId;

  @Column(name = "request_id")
  private String requestId;

  @Column(name = "enc_customer_phone", columnDefinition = "VARCHAR")
  @Convert(converter = PhoneNumberConverter.class)
  private PhoneNumber encCustomerPhone;

  @Column(name = "send_channel")
  @Enumerated(EnumType.STRING)
  private SendChannel sendChannel;

  @Column(name = "template_name")
  private String templateName;

  @Column(name = "send_type")
  @Enumerated(EnumType.STRING)
  private SendType sendType;

  @Column(name = "template_code")
  private String templateCode;

  @Column(name = "content")
  private String content;

  @Column(name = "buttons", columnDefinition = "text")
  private String buttons;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private SendStatus status;

  @Column(name = "fail_code")
  private String failCode;

  @Column(name = "fail_reason")
  private String failReason;

  @Column(name = "send_date_time")
  private ZonedDateTime sendDateTime;

  @Builder
  public MessageSendHistoryEntity(Long seq, Long waitingHistorySeq, String waitingId,
      String requestId, PhoneNumber encCustomerPhone, SendChannel sendChannel, String templateName,
      SendType sendType, String templateCode, String content, String buttons, SendStatus status,
      String failCode, String failReason, ZonedDateTime sendDateTime) {
    this.seq = seq;
    this.waitingHistorySeq = waitingHistorySeq;
    this.waitingId = waitingId;
    this.requestId = requestId;
    this.encCustomerPhone = encCustomerPhone;
    this.sendChannel = sendChannel;
    this.templateName = templateName;
    this.sendType = sendType;
    this.templateCode = templateCode;
    this.content = content;
    this.buttons = buttons;
    this.status = status;
    this.failCode = failCode;
    this.failReason = failReason;
    this.sendDateTime = sendDateTime;
  }
}
