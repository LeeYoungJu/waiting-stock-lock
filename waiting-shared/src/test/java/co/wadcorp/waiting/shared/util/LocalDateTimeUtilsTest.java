package co.wadcorp.waiting.shared.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocalDateTimeUtilsTest {

  @DisplayName("yyyy-MM-dd HH:mm:ss 형태의 문자열을 LocalDateTime으로 변환한다.")
  @Test
  void parseToLocalDate() {
    // given
    String localDateTimeString = "2023-02-15T17:19:03";

    // when
    LocalDateTime localDateTime = LocalDateTimeUtils.parseToLocalDateTime(localDateTimeString);

    // then
    assertThat(localDateTime).isEqualTo(LocalDateTime.of(2023, 2, 15, 17, 19, 3));
  }

}