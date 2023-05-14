package co.wadcorp.waiting.data.support;

import java.math.BigDecimal;

public record Price(BigDecimal value) {

  public static final Price ZERO = Price.of(0);

  public static Price of(long value) {
    return new Price(BigDecimal.valueOf(value));
  }

  public static Price of(BigDecimal value) {
    return new Price(value);
  }

  public Price times(int number) {
    return Price.of(
        value.multiply(BigDecimal.valueOf(number))
    );
  }

  public Price add(Price price) {
    return Price.of(
        value.add(price.value)
    );
  }

}
