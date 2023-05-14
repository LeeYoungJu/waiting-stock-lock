package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.exception.AppException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeSettingsDataValidator {

  private static final int MIN_TABLE_MODE_SEAT_TYPE = 2;
  private static final int MAX_TABLE_MODE_SEAT_TYPE = 4;

  public static void validateTableModeSeatOptions(List<SeatOptions> tableModeSettings) {
    if (tableModeSettings == null) {
      return;
    }

    checkSize(tableModeSettings);
    checkDuplicatedNames(tableModeSettings);
  }

  private static void checkSize(List<SeatOptions> tableModeSettings) {
    if (isSizeInRange(tableModeSettings.size())) {
      throw new AppException(HttpStatus.BAD_REQUEST, String.format(
          "좌석 종류는 %s개 이상 %s개 이하여야 합니다.",
          MIN_TABLE_MODE_SEAT_TYPE, MAX_TABLE_MODE_SEAT_TYPE
      ));
    }
  }

  private static boolean isSizeInRange(int seatTypeSize) {
    return seatTypeSize < MIN_TABLE_MODE_SEAT_TYPE
        || seatTypeSize > MAX_TABLE_MODE_SEAT_TYPE;
  }

  private static void checkDuplicatedNames(List<SeatOptions> tableModeSettings) {
    Set<String> deduplicatedSeatNames = tableModeSettings.stream()
        .map(SeatOptions::getName)
        .collect(Collectors.toSet());

    if (tableModeSettings.size() != deduplicatedSeatNames.size()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "좌석명은 중복될 수 없습니다.");
    }
  }

}
