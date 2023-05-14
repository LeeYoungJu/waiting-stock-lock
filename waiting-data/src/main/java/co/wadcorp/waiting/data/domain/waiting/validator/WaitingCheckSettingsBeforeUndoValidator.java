package co.wadcorp.waiting.data.domain.waiting.validator;

import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingCheckSettingsBeforeUndoValidator {

  public static void validate(WaitingEntity waiting, HomeSettingsEntity homeSettings,
      OptionSettingsEntity optionSettings) {
    ZonedDateTime waitingRegDateTime = waiting.getRegDateTime();
    ZonedDateTime homeSettingsRegDateTime = homeSettings.getRegDateTime();
    ZonedDateTime optionSettingsRegDateTime = optionSettings.getRegDateTime();

    compareRegDateTimes(waitingRegDateTime, homeSettingsRegDateTime);
    compareRegDateTimes(waitingRegDateTime, optionSettingsRegDateTime);
  }

  private static void compareRegDateTimes(ZonedDateTime waitingRegDateTime,
      ZonedDateTime settingsRegDateTime) {
    if (waitingRegDateTime.isBefore(settingsRegDateTime)) {
      throw new AppException(HttpStatus.BAD_REQUEST,
          ErrorCode.CANNOT_UNDO_CAUSE_SEAT_OPTIONS_MODIFIED);
    }
  }

}
