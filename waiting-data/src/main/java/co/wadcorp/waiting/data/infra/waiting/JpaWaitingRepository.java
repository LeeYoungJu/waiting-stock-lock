package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingRepository;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWaitingRepository extends WaitingRepository,
    JpaRepository<WaitingEntity, Long> {

  @Query(""" 
          select w
          from WaitingEntity w
          where w.operationDate = :operationDate 
            and w.customerSeq = :customerSeq
            and w.waitingStatus = :waitingStatus
      """)
  List<WaitingEntity> findAllByCustomerSeqAndStatusToday(Long customerSeq,
      WaitingStatus waitingStatus,
      LocalDate operationDate);


  @Query(""" 
          select count(seq)
          from WaitingEntity
          where operationDate = :operationDate 
            and shopId = :shopId
            and waitingStatus = :waitingStatus
            and seatOptionName = :seatOptionName
      """)
  int countAllWaitingTeamBySeatOption(String shopId, WaitingStatus waitingStatus,
      String seatOptionName,
      LocalDate operationDate);

  @Query("""
          select max(w.waitingNumbers.waitingOrder)
          from WaitingEntity w
          where w.shopId = :shopId
            and w.operationDate = :operationDate
            and w.waitingStatus = :waitingStatus
      """)
  Optional<Integer> findMaxWaitingOrderByShopId(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus);

  List<WaitingEntity> findAllByOperationDateAndWaitingStatusAndExpectedSittingDateTimeIsBetween(
      LocalDate operationDate, WaitingStatus waiting, ZonedDateTime before, ZonedDateTime after);

  @Query("""
          select w.waitingId
          from WaitingEntity w
          where w.shopId = :shopId
            and w.operationDate = :operationDate
            and w.seatOptionName = :seatOptionName
            and w.waitingNumbers.waitingOrder >= :waitingOrder
      """)
  List<String> findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
      String shopId, LocalDate operationDate, String seatOptionName, int waitingOrder
  );


  List<WaitingEntity> findAllByWaitingIdInAndOperationDate(List<String> waitingIds, LocalDate operationDate);

}
