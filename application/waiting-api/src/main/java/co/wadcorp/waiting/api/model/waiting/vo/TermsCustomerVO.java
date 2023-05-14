package co.wadcorp.waiting.api.model.waiting.vo;

import co.wadcorp.waiting.data.domain.customer.TermsCustomerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermsCustomerVO {

  private Integer seq;
  private Boolean isAgree;

  public TermsCustomerEntity toTermsCustomerEntity(String shopId, Long waitingSeq, Long customerSeq) {
    return TermsCustomerEntity.builder()
        .shopId(shopId)
        .customerSeq(customerSeq)
        .waitingSeq(waitingSeq)
        .termsSeq(seq)
        .isAgree(isAgree)
        .build();
  }
}
