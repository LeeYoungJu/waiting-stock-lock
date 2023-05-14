package co.wadcorp.waiting.data.domain.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.shop.operation.pause.AutoPauseInfo;
import co.wadcorp.waiting.shared.util.PhoneNumberUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AutoPauseInfoTest {

  @DisplayName("")
  @Test
  void test() {
      // given
    PhoneNumber phoneNumber = PhoneNumberUtils.ofKr(null);
    assertThat(phoneNumber).isNull();

    // when

      // then
  }

  @DisplayName("자동 일시정지 시간 내에 해당하는지 체크한다.")
  @CsvSource({
      "2023-02-22T13:59:59, false",
      "2023-02-22T14:00:00, true",
      "2023-02-22T14:00:01, true",
      "2023-02-22T15:00:00, true",
      "2023-02-22T15:00:01, false",
  })
  @ParameterizedTest
  void isBetweenAutoPauseRange(LocalDateTime now, boolean expected) {
    // given
    ZonedDateTime zonedNow = ZonedDateTimeUtils.ofSeoul(now);

    ZonedDateTime autoPauseStartDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(2023, 2, 22, 14, 0));
    ZonedDateTime autoPauseEndDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(2023, 2, 22, 15, 0));

    AutoPauseInfo autoPauseInfo = AutoPauseInfo.builder()
        .autoPauseStartDateTime(autoPauseStartDateTime)
        .autoPauseEndDateTime(autoPauseEndDateTime)
        .build();

    // when
    boolean result = autoPauseInfo.isBetweenAutoPauseRange(zonedNow);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @DisplayName("자동 일시정지 시간 내에 해당하는지 체크한다. - null인 경우 false")
  @Test
  void isBetweenAutoPauseRange() {
    // given
    ZonedDateTime zonedNow = null;

    ZonedDateTime autoPauseStartDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(2023, 2, 22, 14, 0));
    ZonedDateTime autoPauseEndDateTime = ZonedDateTimeUtils.ofSeoul(
        LocalDateTime.of(2023, 2, 22, 15, 0));

    AutoPauseInfo autoPauseInfo = AutoPauseInfo.builder()
        .autoPauseStartDateTime(autoPauseStartDateTime)
        .autoPauseEndDateTime(autoPauseEndDateTime)
        .build();

    // when
    boolean result = autoPauseInfo.isBetweenAutoPauseRange(zonedNow);

    // then
    assertThat(result).isFalse();
  }

}