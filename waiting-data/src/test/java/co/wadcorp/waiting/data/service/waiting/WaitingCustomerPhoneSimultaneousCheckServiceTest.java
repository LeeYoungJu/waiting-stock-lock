package co.wadcorp.waiting.data.service.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class WaitingCustomerPhoneSimultaneousCheckServiceTest extends IntegrationTest {

  @Autowired
  private WaitingCustomerPhoneSimultaneousCheckService waitingCustomerPhoneSimultaneousCheckService;

  @DisplayName("전화번호 등록 시 동시성 체크 (로컬 Redis)")
  @Test
  void isSimultaneous() throws InterruptedException {
    // given
    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr("010-0000-0000");
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 20);

    given(phoneNumberConverter.convertToDatabaseColumn(phoneNumber)).willReturn("암호화된번호");

    int threadCount = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    List<AtomicBoolean> results = new ArrayList<>(List.of());
    for (int index = 0; index < threadCount; index++) {
      results.add(new AtomicBoolean(true));
    }

    // when
    results.forEach(result -> {
      executorService.execute(() -> {
        result.set(waitingCustomerPhoneSimultaneousCheckService.isSimultaneous(phoneNumber, shopId,
            operationDate));
        latch.countDown();
      });
    });
    latch.await();

    // then
    assertThat(results)
        .filteredOn(atomicBoolean -> !atomicBoolean.get()) // false 는 단 한개
        .hasSize(1);
  }

}