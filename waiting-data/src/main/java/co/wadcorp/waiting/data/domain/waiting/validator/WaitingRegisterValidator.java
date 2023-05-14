package co.wadcorp.waiting.data.domain.waiting.validator;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingRegisterValidator {

  private static final Integer MAXIMUM_WAITING_SIZE = 3;

  public static void validate(String shopId, List<WaitingEntity> waitings) {

    validateWaitingSameShop(shopId, waitings);
    validateWaitingMaxCount(waitings);
  }

  /**
   * 같은 매장에 이미 등록된 웨이팅이 존재하면 등록할 수 없다.
   */
  private static void validateWaitingSameShop(String shopId, List<WaitingEntity> waitings) {
    waitings.stream()
        .filter(w -> StringUtils.equals(w.getShopId(), shopId))
        .findAny()
        .ifPresent(w -> {
          throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.ALREADY_REGISTERED_WAITING);
        });
  }

  /**
   * 웨이팅은 동시에 3개를 초과할 수 없다.
   */
  private static void validateWaitingMaxCount(List<WaitingEntity> waitings) {
    if (waitings.size() >= MAXIMUM_WAITING_SIZE) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NO_MORE_THAN_THREE_TIMES);
    }
  }


}
