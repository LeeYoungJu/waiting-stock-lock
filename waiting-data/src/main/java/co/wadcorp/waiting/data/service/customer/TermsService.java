package co.wadcorp.waiting.data.service.customer;

import co.wadcorp.waiting.data.domain.customer.TermsEntity;
import co.wadcorp.waiting.data.domain.customer.TermsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TermsService {

  private final TermsRepository termsRepository;

  public List<TermsEntity> getAllWaitingTerm() {
    return termsRepository.findAllByIsUsedIsTrue();
  }
}
