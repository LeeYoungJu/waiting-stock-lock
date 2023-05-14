package co.wadcorp.waiting.data.infra.customer;

import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.CustomerRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCustomerRepository extends CustomerRepository, JpaRepository<CustomerEntity, Long> {

  List<CustomerEntity> findAllBySeqIn(List<Long> seqs);

}
