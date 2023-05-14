package co.wadcorp.waiting.data.service.customer;

import co.wadcorp.waiting.data.domain.customer.TermsCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.TermsCustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TermsCustomerService {

  private final TermsCustomerRepository termsCustomerRepository;

  public void saveAllTermsCustomer(List<TermsCustomerEntity> termsCustomerEntityList) {
    termsCustomerRepository.saveAll(termsCustomerEntityList);
  }
}
