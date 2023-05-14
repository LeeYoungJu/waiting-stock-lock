package co.wadcorp.waiting.data.domain.customer;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "cw_terms")
public class TermsEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Integer seq;

  @Column(name = "terms_code")
  private String termsCode;

  @Column(name = "terms_subject")
  private String termsSubject;

  @Column(name = "terms_content")
  private String termsContent;

  @Column(name = "terms_version")
  private String termsVersion;

  @Column(name = "terms_url")
  private String termsUrl;

  @Column(name = "terms_order")
  private Integer termsOrder;

  @Column(name = "use_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isUsed;

  @Column(name = "required_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isRequired;

  @Column(name = "marketing_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isMarketing;

  @Column(name = "publish_date_time")
  private ZonedDateTime publishDateTime;

  @Column(name = "apply_date_time")
  private LocalDateTime applyDateTime;

}
