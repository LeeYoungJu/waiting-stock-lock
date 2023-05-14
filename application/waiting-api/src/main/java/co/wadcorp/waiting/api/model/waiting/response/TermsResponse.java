package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.waiting.data.domain.customer.TermsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermsResponse {

  private Integer seq;
  private String termsSubject;
  private String termsContent;
  private String termsUrl;
  private Boolean isRequired;
  private Boolean isMarketing;

  public static TermsResponse toDto(TermsEntity terms) {
    return TermsResponse.builder()
        .seq(terms.getSeq())
        .termsSubject(terms.getTermsSubject())
        .termsContent(terms.getTermsContent())
        .termsUrl(terms.getTermsUrl())
        .isRequired(terms.getIsRequired())
        .isMarketing(terms.getIsMarketing())
        .build();
  }
}
