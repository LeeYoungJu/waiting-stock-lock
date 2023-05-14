package co.wadcorp.waiting.data.domain.waiting;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository {

  WaitingEntity save(WaitingEntity waitingEntity);

  <S extends WaitingEntity> List<S> saveAll(Iterable<S> entities);

  List<WaitingEntity> findAll();

  Optional<WaitingEntity> findByWaitingId(String waitingId);

  List<WaitingEntity> findAllByWaitingIdInAndOperationDate(List<String> waitingIds, LocalDate operationDate);

  List<WaitingEntity> findAllByCustomerSeqAndStatusToday(Long customerSeq, WaitingStatus status,
      LocalDate operationDate);

  int countAllWaitingTeamBySeatOption(String shopId, WaitingStatus waitingStatus,
      String seatOptionName, LocalDate operationDate);

  boolean existsByShopIdAndWaitingStatusAndOperationDate(String shopId, WaitingStatus waitingStatus,
      LocalDate operationDateFromNow);

  List<WaitingEntity> findAllByShopIdAndOperationDate(String shopId, LocalDate operationDate);

  List<WaitingEntity> findAllByShopIdAndOperationDateAndWaitingStatus(String shopId, LocalDate operationDate, WaitingStatus waitingStatus);

  Optional<Integer> findMaxWaitingOrderByShopId(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus);

  List<WaitingEntity> findAllByOperationDateAndWaitingStatusAndExpectedSittingDateTimeIsBetween(
      LocalDate operationDate, WaitingStatus waiting, ZonedDateTime before, ZonedDateTime after);

  List<String> findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
      String shopId, LocalDate operationDate, String seatOptionName, int waitingOrder
  );

  void deleteAllInBatch();
}
