package co.wadcorp.waiting.api.internal.service.waiting.validator;

import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;

public class RemoteOrderMenuValidator {

  public static void validate(RemoteOrderDto orderDto) {
    // 메뉴별 가격 정보 validate
    if (!orderDto.isLineItemsPriceValid()) {
      throw AppException.ofBadRequest(ErrorCode.INVALID_LINE_ITEM_PRICE);
    }
    // 최종 가격 정보 validate
    if(!orderDto.isTotalPriceValid()) {
      throw AppException.ofBadRequest(ErrorCode.INVALID_TOTAL_PRICE);
    }
  }
}
