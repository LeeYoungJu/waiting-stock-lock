package co.wadcorp.waiting.data.domain.customer;

import co.wadcorp.libs.phone.PhoneNumber;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

  Optional<CustomerEntity> findById(Long customerSeq);

  List<CustomerEntity> findAllBySeqIn(List<Long> seqs);

  CustomerEntity save(CustomerEntity customerEntity);

  Optional<CustomerEntity> findByEncCustomerPhone(PhoneNumber customerPhone);

}
