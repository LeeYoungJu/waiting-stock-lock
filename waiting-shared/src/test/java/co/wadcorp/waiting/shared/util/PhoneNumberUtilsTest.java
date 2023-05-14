package co.wadcorp.waiting.shared.util;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.phone.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneNumberUtilsTest {

  @DisplayName("빈 문자열을 전화번호로 변환 시 변환에는 성공하나 유효하지 않은 상태다.")
  @Test
  void empty() {
    // given
    String invalidPhoneNumber = "";

    // when
    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(invalidPhoneNumber);

    // then
    assertThat(phoneNumber).isNotNull();
    assertThat(phoneNumber.isValid()).isFalse();
  }

}