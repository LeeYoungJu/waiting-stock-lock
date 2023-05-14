package co.wadcorp.waiting.data.domain.customer;

import java.util.List;

public interface TermsCustomerRepository {

  <S extends TermsCustomerEntity> List<S> saveAll(Iterable<S> entities);
}
