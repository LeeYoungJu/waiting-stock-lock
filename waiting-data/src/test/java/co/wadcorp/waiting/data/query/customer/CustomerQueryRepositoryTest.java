package co.wadcorp.waiting.data.query.customer;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import co.wadcorp.waiting.data.domain.customer.CustomerEntity;
import co.wadcorp.waiting.data.domain.customer.CustomerRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CustomerQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private CustomerQueryRepository customerQueryRepository;

  @Autowired
  private WaitingRepository waitingRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @DisplayName("웨이팅 ID로 고객을 조회한다.")
  @Test
  void getCustomerBy() {
    // given
    PhoneNumber customerPhone = PhoneNumberUtils.ofKr("010-1234-1234");
    CustomerEntity customer = createCustomer(customerPhone);

    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 2, 24);

    WaitingEntity waiting = createWaiting(shopId, operationDate, WaitingStatus.WAITING,
        WaitingDetailStatus.WAITING, customer.getSeq());

    // when
    Optional<CustomerEntity> result = customerQueryRepository.getCustomerBy(
        waiting.getWaitingId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get())
        .extracting("encCustomerPhone")
        .isEqualTo(customerPhone);
  }

  @DisplayName("웨이팅 ID로 고객 조회 시 없으면 null을 반환한다.")
  @Test
  void getCustomerNull() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 2, 24);

    Long customerSeq = null;
    WaitingEntity waiting = createWaiting(shopId, operationDate, WaitingStatus.WAITING,
        WaitingDetailStatus.WAITING, customerSeq);

    // when
    Optional<CustomerEntity> result = customerQueryRepository.getCustomerBy(
        waiting.getWaitingId());

    // then
    assertThat(result).isEmpty();
  }

  private CustomerEntity createCustomer(PhoneNumber encCustomerPhone) {
    CustomerEntity customer = CustomerEntity.builder()
        .encCustomerPhone(encCustomerPhone)
        .build();
    return customerRepository.save(customer);
  }

  private WaitingEntity createWaiting(
      String shopId, LocalDate operationDate, WaitingStatus waitingStatus,
      WaitingDetailStatus waitingDetailStatus, Long customerSeq) {
    WaitingEntity waiting = WaitingEntity.builder()
        .shopId(shopId)
        .waitingId(UUIDUtil.shortUUID().toUpperCase())
        .operationDate(operationDate)
        .waitingStatus(waitingStatus)
        .waitingDetailStatus(waitingDetailStatus)
        .seatOptionName("홀")
        .customerSeq(customerSeq)
        .waitingCompleteDateTime(null)
        .waitingNumbers(
            WaitingNumber.builder()
                .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
                .waitingOrder(1)
                .build()
        )
        .build();
    return waitingRepository.save(waiting);
  }

}