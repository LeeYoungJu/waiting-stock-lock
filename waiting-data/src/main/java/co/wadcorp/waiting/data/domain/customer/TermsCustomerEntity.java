package co.wadcorp.waiting.data.domain.customer;

import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cw_terms_customer")
public class TermsCustomerEntity extends BaseHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "waiting_seq")
  private Long waitingSeq;

  @Column(name = "customer_seq")
  private Long customerSeq;

  @Column(name = "terms_seq")
  private Integer termsSeq;

  @Column(name = "agree_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isAgree;

  @Builder
  public TermsCustomerEntity(String shopId, Long waitingSeq, Long customerSeq, Integer termsSeq, Boolean isAgree) {
    this.shopId = shopId;
    this.waitingSeq = waitingSeq;
    this.customerSeq = customerSeq;
    this.termsSeq = termsSeq;
    this.isAgree = isAgree;
  }
}
