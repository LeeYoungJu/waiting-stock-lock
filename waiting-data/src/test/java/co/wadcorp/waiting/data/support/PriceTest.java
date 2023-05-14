package co.wadcorp.waiting.data.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceTest {

  @DisplayName("금액 객체는 곱하면 새로운 인스턴스가 생성된다.")
  @Test
  void times() {
    // given
    Price price = Price.of(BigDecimal.valueOf(1));

    // when
    Price times = price.times(2);

    // then
    assertThat(times.value().intValue()).isEqualTo(2);
    assertThat(times).isNotSameAs(price);
  }

}