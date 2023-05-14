package co.wadcorp.waiting.shared.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZonedDateTimeUtils {

  private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");

  public static ZonedDateTime nowOfSeoul() {
    return ofSeoul(LocalDateTime.now());
  }

  public static ZonedDateTime ofSeoul(LocalDate localDate, LocalTime localTime) {
    return localDate == null || localTime == null
        ? null
        : ZonedDateTime.of(localDate, localTime, SEOUL_ZONE_ID);
  }

  public static ZonedDateTime ofSeoul(LocalDateTime localDateTime) {
    return localDateTime == null
        ? null
        : ZonedDateTime.of(localDateTime, SEOUL_ZONE_ID);
  }

  public static Timestamp convertToTimestamp(ZonedDateTime zonedDateTime) {
    return zonedDateTime == null
        ? null
        : Timestamp.from(zonedDateTime.toInstant());
  }


  public static boolean isBetween(ZonedDateTime nowDateTime, ZonedDateTime startDate, ZonedDateTime endDateTime) {
    if (Objects.isNull(nowDateTime)
        || Objects.isNull(startDate)
        || Objects.isNull(endDateTime)) {
      return false;
    }

    return startDate.isBefore(nowDateTime) && endDateTime.isAfter(nowDateTime);
  }

}
