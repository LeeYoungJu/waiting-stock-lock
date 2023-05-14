package co.wadcorp.waiting.data.service.waiting;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.infra.waiting.DistributedLockRedisTemplate;
import co.wadcorp.waiting.data.support.PhoneNumberConverter;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WaitingCustomerPhoneSimultaneousCheckService {

  private final DistributedLockRedisTemplate distributedLockRedisTemplate;
  private final PhoneNumberConverter phoneNumberConverter;

  public Boolean isSimultaneous(PhoneNumber phoneNumber, String shopId, LocalDate operationDate) {
    if (!phoneNumber.isValid()) {
      throw new IllegalArgumentException("유효하지 않은 전화번호입니다.");
    }

    String encryptedPhoneNumber = phoneNumberConverter.convertToDatabaseColumn(phoneNumber);
    return distributedLockRedisTemplate.setPhoneNumberIfAbsent(shopId, operationDate,
        encryptedPhoneNumber);
  }

}
