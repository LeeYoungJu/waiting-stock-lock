package co.wadcorp.waiting.shared.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocalDateUtilsTest {

  @DisplayName("yyyy-MM-dd 형태의 문자열을 LocalDate로 변환한다.")
  @Test
  void parseToLocalDate() {
    // given
    String localDateString = "2023-02-15";

    // when
    LocalDate localDate = LocalDateUtils.parseToLocalDate(localDateString);

    // then
    assertThat(localDate).isEqualTo(LocalDate.of(2023, 2, 15));
  }

  @DisplayName("시작일자와 종료일자를 주면 해당하는 기간의 모든 날짜를 반환한다.")
  @Test
  void getRangeBy() {
    // given
    LocalDate startDate = LocalDate.of(2023, 4, 24);
    LocalDate endDate = LocalDate.of(2023, 4, 28);

    // when
    List<LocalDate> localDates = LocalDateUtils.getRangeBy(startDate, endDate);

    // then
    assertThat(localDates).isEqualTo(List.of(
        startDate,
        LocalDate.of(2023, 4, 25),
        LocalDate.of(2023, 4, 26),
        LocalDate.of(2023, 4, 27),
        endDate
    ));
  }

}