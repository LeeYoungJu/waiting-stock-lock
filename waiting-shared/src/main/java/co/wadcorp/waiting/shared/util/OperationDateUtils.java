package co.wadcorp.waiting.shared.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class OperationDateUtils {

  private static final LocalTime STANDARD_TIME = LocalTime.of(6, 0);

  private OperationDateUtils() {
  }

  public static LocalDate getOperationDate(LocalDateTime localDateTime) {
    LocalDate localDate = localDateTime.toLocalDate();
    LocalTime localTime = localDateTime.toLocalTime();

    if (STANDARD_TIME.isAfter(localTime)) {
      return localDate.minusDays(1L);
    }
    return localDate;
  }

  public static LocalDate getOperationDateFromNow() {
    LocalDateTime now = LocalDateTime.now();
    return getOperationDate(now);
  }

}
