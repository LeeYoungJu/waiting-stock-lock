package co.wadcorp.waiting.data.domain.message;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cw_message_sms_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsTemplateEntity extends BaseEntity {

  public static final SmsTemplateEntity EMPTY_TEMPLATE = new SmsTemplateEntity();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "template_name")
  private String templateName;

  @Column(name = "send_type")
  @Enumerated(EnumType.STRING)
  private SendType sendType;

  @Column(name = "template_content")
  private String templateContent;

  @Column(name = "is_used", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isUsed;

}
