package co.wadcorp.waiting.api.service.waiting;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.api.model.waiting.response.TermsResponse;
import co.wadcorp.waiting.data.domain.customer.TermsEntity;
import co.wadcorp.waiting.data.service.customer.TermsService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WaitingTermsApiService {

  private final TermsService termsService;

  public List<TermsResponse> getAllWaitingTerms() {
    List<TermsEntity> terms = termsService.getAllWaitingTerm();
    return convert(terms, TermsResponse::toDto);
  }
}
