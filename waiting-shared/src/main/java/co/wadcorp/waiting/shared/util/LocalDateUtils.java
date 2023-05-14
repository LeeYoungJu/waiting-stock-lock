package co.wadcorp.waiting.shared.util;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateUtils {

  private static final DateTimeFormatter DATE_TIME_FORMATTER
      = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static LocalDate parseToLocalDate(String localDateString) {
    return LocalDate.parse(localDateString, DATE_TIME_FORMATTER);
  }

  public static String convertToString(LocalDate localDate) {
    return DATE_TIME_FORMATTER.format(localDate);
  }

  public static List<LocalDate> getRangeBy(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate, date -> date.plusDays(1))
        .limit(DAYS.between(startDate, endDate.plusDays(1)))
        .toList();
  }

}
