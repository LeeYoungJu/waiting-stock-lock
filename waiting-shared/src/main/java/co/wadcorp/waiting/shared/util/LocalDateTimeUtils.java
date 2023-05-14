package co.wadcorp.waiting.shared.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateTimeUtils {

  private static final DateTimeFormatter DATE_TIME_FORMATTER
      = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  public static LocalDateTime parseToLocalDateTime(String localDateTimeString) {
    return StringUtils.hasText(localDateTimeString)
        ? LocalDateTime.parse(localDateTimeString, DATE_TIME_FORMATTER)
        : null;
  }

  public static String convertToString(LocalDateTime localDateTime) {
    return DATE_TIME_FORMATTER.format(localDateTime);
  }

  public static Timestamp convertToTimestamp(LocalDateTime localDateTime) {
    return localDateTime == null
        ? null
        : Timestamp.valueOf(localDateTime);
  }

  public static Timestamp convertToTimestamp(ZonedDateTime zonedDateTime) {
    return zonedDateTime == null
        ? null
        : convertToTimestamp(zonedDateTime.toLocalDateTime());
  }

}
