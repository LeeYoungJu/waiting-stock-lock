package co.wadcorp.waiting.shared.util;

import co.wadcorp.libs.phone.PhoneNumber;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneNumberUtils {

  public static PhoneNumber ofKr(String phoneNumber) { // Nullable
    return phoneNumber != null
        ? new PhoneNumber("KR", phoneNumber)
        : null;
  }

}
