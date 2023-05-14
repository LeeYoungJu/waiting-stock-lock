package co.wadcorp.waiting.api.internal.service.waiting.validator;

import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;

public class RemoteOrderSettingsValidator {

  public static void validate(OrderSettingsData orderSettings) {
    // 매장의 선주문 사용여부 설정값 validate
    if (!orderSettings.isPossibleOrder()) {
      throw AppException.ofBadRequest(ErrorCode.NOT_POSSIBLE_ORDER);
    }
  }
}
