package co.wadcorp.waiting.data.infra.customer;

import co.wadcorp.waiting.data.domain.customer.TermsCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.TermsCustomerRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTermsCustomerRepository extends JpaRepository<TermsCustomerEntity, Long>, TermsCustomerRepository {
  @Override
  <S extends TermsCustomerEntity> List<S> saveAll(Iterable<S> entities);
}
