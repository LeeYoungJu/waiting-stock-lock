package co.wadcorp.waiting.shared.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class OperationDateTimeUtils {

  public static final LocalTime NEXT_DAY_RANGE_START = LocalTime.of(0, 0);
  public static final LocalTime NEXT_DAY_RANGE_END = LocalTime.of(6, 0);

  public static ZonedDateTime getCalculateOperationDateTime(LocalDate operationDate,
      LocalTime localTime) {

    if (isNextDay(localTime)) {
      operationDate = operationDate.plusDays(1);
    }

    return ZonedDateTimeUtils.ofSeoul(operationDate, localTime);
  }

  private static boolean isNextDay(LocalTime localTime) {
    if (localTime.equals(NEXT_DAY_RANGE_START)) {
      return true;
    }
    return localTime.isAfter(NEXT_DAY_RANGE_START)
        && localTime.isBefore(NEXT_DAY_RANGE_END);
  }
}
