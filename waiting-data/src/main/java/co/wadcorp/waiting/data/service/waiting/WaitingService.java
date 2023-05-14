package co.wadcorp.waiting.data.service.waiting;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.validator.WaitingCancelValidator;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class WaitingService {

  private final WaitingRepository waitingRepository;
  private final WaitingHistoryRepository waitingHistoryRepository;

  public WaitingHistoryEntity saveWaiting(WaitingEntity waitingEntity) {
    WaitingEntity waiting = waitingRepository.save(waitingEntity);
    return waitingHistoryRepository.save(transFromWaitingToHistory(waiting));
  }

  public WaitingEntity findByWaitingId(String waitingId) {
    return waitingRepository.findByWaitingId(waitingId)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_WAITING));
  }

  public List<WaitingEntity> findAllByShopIdAndOperationDate(String shopId,
      LocalDate operationDate) {
    return waitingRepository.findAllByShopIdAndOperationDate(shopId, operationDate);
  }

  public List<WaitingEntity> getWaitingByCustomerSeqToday(Long customerSeq,
      LocalDate operationDate) {
    return waitingRepository.findAllByCustomerSeqAndStatusToday(customerSeq, WaitingStatus.WAITING,
        operationDate);
  }

  public boolean existWaitingTeamByShopId(String shopId, LocalDate operationDate) {
    return waitingRepository.existsByShopIdAndWaitingStatusAndOperationDate(shopId,
        WaitingStatus.WAITING, operationDate);
  }

  public boolean validWaitingTeamExists(String shopId, LocalDate operationDate) {
    if (existWaitingTeamByShopId(shopId, operationDate)) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.COULD_NOT_UPDATE_SETTINGS);
    }
    return false;
  }

  public WaitingEntity cancelByCustomer(String waitingId) {
    WaitingEntity waiting = findByWaitingId(waitingId);

    WaitingCancelValidator.validateStatus(waiting);

    waiting.cancelByCustomer();
    waitingHistoryRepository.save(transFromWaitingToHistory(waiting));

    return waiting;
  }

  public List<WaitingEntity> findDelayedWaiting(LocalDate operationDate,
      ZonedDateTime nowZonedDateTime) {
    ZonedDateTime after = nowZonedDateTime.truncatedTo(ChronoUnit.MINUTES);
    ZonedDateTime before = after.minusMinutes(1);

    return waitingRepository.findAllByOperationDateAndWaitingStatusAndExpectedSittingDateTimeIsBetween(
        operationDate, WaitingStatus.WAITING, before, after);
  }

  private WaitingHistoryEntity transFromWaitingToHistory(WaitingEntity waiting) {
    return new WaitingHistoryEntity(waiting);
  }

  public List<WaitingEntity> findAllByShopIdAndOperationDateAndWaitingStatus(String shopId,
      LocalDate operationDate, WaitingStatus waitingStatus) {
    return waitingRepository.findAllByShopIdAndOperationDateAndWaitingStatus(shopId, operationDate,
        waitingStatus);
  }

  public List<String> findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
      String shopId,
      LocalDate operationDate,
      String seatOptionName,
      int waitingOrder
  ) {
    return waitingRepository.findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
        shopId,
        operationDate,
        seatOptionName,
        waitingOrder
    );
  }
}
