package co.wadcorp.waiting.data.domain.waiting.validator;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingShopIdValidator {

  public static void validateWaitingSameShopId(String shopId, WaitingEntity waiting) {
    if (shopId.equals(waiting.getShopId())) {
      return;
    }
    throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_MATCH_SHOP_ID);
  }

}
