package co.wadcorp.waiting.api.service.customer;

import co.wadcorp.waiting.data.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerApiService {

  private final CustomerService customerService;


}
