package co.wadcorp.waiting.data.service.waiting;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntities;
import co.wadcorp.waiting.data.domain.waiting.WaitingNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.infra.waiting.DistributedLockRedisTemplate;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WaitingNumberService {

  private final DistributedLockRedisTemplate distributedLockRedisTemplate;
  private final WaitingRepository waitingRepository;

  public WaitingNumber getWaitingNumber(String shopId, LocalDate operationDate) {

    return createWaitingNumber(shopId, operationDate, (sid, od) -> {
      WaitingEntities waitingEntities = new WaitingEntities(
          waitingRepository.findAllByShopIdAndOperationDate(sid, od));

      return waitingEntities.createWaitingNumber();
    });
  }

  private WaitingNumber createWaitingNumber(String shopId, LocalDate operationDate,
      BiFunction<String, LocalDate, WaitingNumber> fallbackWaitingNumber) {

    try {
      Long waitingOrder = incrementGetWaitingOrder(shopId, operationDate);
      Long waitingNumber = createWaitingNumber(
          incrementGetWaitingNumber(shopId, operationDate),
          operationDate
      );

      return WaitingNumber.builder()
          .waitingOrder(waitingOrder.intValue())
          .waitingNumber(waitingNumber.intValue())
          .build();

    } catch (Exception e) {
      WaitingNumber waitingNumber = fallbackWaitingNumber.apply(shopId, operationDate);
      // TODO 레디스 장애 발생시 DB에서 발급받은 waitingOrder를 최신 번호로 설정하기 - 장애가 해소될 때 설정할 수 있는 방안을 찾아야함
      //valueOps.set(String.format(WAITING_NUMBER_KEY, shopId, operationDate), waitingNumber.getWaitingOrder());

      return waitingNumber;
    }
  }

  public Long incrementGetWaitingNumber(String shopId, LocalDate operationDate) {
    return distributedLockRedisTemplate.incrementWaitingNumber(shopId, operationDate);
  }

  public Long incrementGetWaitingOrder(String shopId, LocalDate operationDate) {
    return distributedLockRedisTemplate.incrementWaitingOrder(shopId, operationDate);
  }

  // TODO 필요할지 확인 필요함
  public Integer getWaitingOrder(String shopId, LocalDate operationDate) {
    return distributedLockRedisTemplate.getWaitingOrder(shopId, operationDate);
  }

  public Integer getMaxWaitingOrder(String shopId, LocalDate operationDate) {
    return waitingRepository.findMaxWaitingOrderByShopId(shopId,
            operationDate,
            WaitingStatus.WAITING
        )
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_WAITING));
  }

  /**
   * 날짜 끝자리 + ## 오름차순 (단, 날짜 끝자리가 0인 경우, 1로 표기)
   */
  public static Long createWaitingNumber(Long waitingOrder, LocalDate operationDate) {
    int date = operationDate.getDayOfMonth();
    int lastNumOfDate = date % 10;

    // 날짜 끝자리가 0인 경우, 1로 시작
    int prefixNumber = lastNumOfDate == 0 ? 1 : lastNumOfDate;

    String waitingNumber = String.format("%d00", prefixNumber);

    return Long.parseLong(waitingNumber) + waitingOrder;
  }
}
