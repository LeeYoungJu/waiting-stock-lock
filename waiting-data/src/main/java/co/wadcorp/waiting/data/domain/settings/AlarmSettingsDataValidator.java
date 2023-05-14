package co.wadcorp.waiting.data.domain.settings;

import co.wadcorp.waiting.data.exception.AppException;
import org.springframework.http.HttpStatus;

public class AlarmSettingsDataValidator {

  private static final int MIN_AUTO_CANCEL_PERIOD = 1;
  private static final int MAX_AUTO_CANCEL_PERIOD = 10;
  private static final int MIN_AUTO_ALARM_ORDERING = 1;
  private static final int MAX_AUTO_ALARM_ORDERING = 99;

  public static void validate(Integer autoCancelPeriod, Integer autoAlarmOrdering) {
    validateAutoCancelPeriod(autoCancelPeriod);
    validateAutoAlarmOrdering(autoAlarmOrdering);
  }

  private static void validateAutoCancelPeriod(Integer autoCancelPeriod) {
    if (autoCancelPeriod == null) {
      throw new AppException(HttpStatus.BAD_REQUEST, "자동취소 시간은 비어있을 수 없습니다.");
    }

    if (autoCancelPeriod < MIN_AUTO_CANCEL_PERIOD || autoCancelPeriod > MAX_AUTO_CANCEL_PERIOD) {
      throw new AppException(HttpStatus.BAD_REQUEST,
          String.format("입력한 자동취소 시간을 확인해주세요. (허용범위: %s ~ %s)",
              MIN_AUTO_CANCEL_PERIOD, MAX_AUTO_CANCEL_PERIOD));
    }
  }

  private static void validateAutoAlarmOrdering(Integer autoAlarmOrdering) {
    if (autoAlarmOrdering == null) {
      throw new AppException(HttpStatus.BAD_REQUEST, "입장준비 알림톡 발송 순서는 비어있을 수 없습니다.");
    }

    if (autoAlarmOrdering < MIN_AUTO_ALARM_ORDERING || autoAlarmOrdering > MAX_AUTO_ALARM_ORDERING) {
      throw new AppException(HttpStatus.BAD_REQUEST,
          String.format("입력한 입장준비 알림톡 발송 순서를 확인해주세요. (허용범위: %s ~ %s)",
              MIN_AUTO_ALARM_ORDERING, MAX_AUTO_ALARM_ORDERING));
    }
  }

}
