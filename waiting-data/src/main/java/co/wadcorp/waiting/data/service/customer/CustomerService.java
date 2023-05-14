package co.wadcorp.waiting.data.service.customer;

import static co.wadcorp.waiting.data.domain.customer.CustomerEntity.EMPTY_CUSTOMER_ENTITY;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.CustomerRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerEntity getCustomerByCustomerPhone(PhoneNumber encCustomerPhone) {
    return customerRepository.findByEncCustomerPhone(encCustomerPhone)
        .orElse(EMPTY_CUSTOMER_ENTITY);
  }

  public CustomerEntity findByCustomerPhone(PhoneNumber encCustomerPhone) {
    return customerRepository.findByEncCustomerPhone(encCustomerPhone)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CUSTOMER));
  }

  public CustomerEntity saveCustomerEntity(CustomerEntity customerEntity) {
    return customerRepository.save(customerEntity);
  }

  public CustomerEntity findById(Long customerSeq) {
    return customerRepository.findById(customerSeq)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_CUSTOMER));
  }

  public List<CustomerEntity> findByIds(List<Long> customerSeqs) {
    return customerRepository.findAllBySeqIn(customerSeqs);
  }

}
