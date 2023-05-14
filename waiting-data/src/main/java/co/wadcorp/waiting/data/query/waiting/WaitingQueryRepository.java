package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.customer.QCustomerEntity.customerEntity;
import static co.wadcorp.waiting.data.domain.customer.QShopCustomerEntity.shopCustomerEntity;
import static co.wadcorp.waiting.data.domain.waiting.QWaitingEntity.waitingEntity;
import static com.querydsl.core.types.Projections.fields;

import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCurrentStatusCountDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class WaitingQueryRepository {

  private final JPAQueryFactory queryFactory;

  public WaitingDto getWaiting(String waitingId) {
    return queryFactory
        .select(fields(
            WaitingDto.class,
            waitingEntity.seq,
            waitingEntity.waitingId,
            waitingEntity.shopId,
            waitingEntity.registerChannel,
            waitingEntity.operationDate,
            waitingEntity.customerSeq,
            customerEntity.encCustomerPhone.as("customerPhoneNumber"),
            waitingEntity.customerName,
            shopCustomerEntity.sittingCount,
            waitingEntity.waitingNumbers.waitingNumber,
            waitingEntity.waitingNumbers.waitingOrder,
            waitingEntity.waitingStatus,
            waitingEntity.waitingDetailStatus,
            waitingEntity.seatOptionName,
            waitingEntity.totalPersonCount.as("totalSeatCount"),
            waitingEntity.personOptionsData.as("personOptions"),
            waitingEntity.expectedSittingDateTime,
            waitingEntity.waitingCompleteDateTime,
            waitingEntity.regDateTime
        ))
        .from(waitingEntity)
        .leftJoin(customerEntity).on(waitingEntity.customerSeq.eq(customerEntity.seq))
        .leftJoin(shopCustomerEntity)
        .on(customerEntity.seq.eq(shopCustomerEntity.shopCustomerId.customerSeq)
            .and(shopCustomerEntity.shopCustomerId.shopId.eq(waitingEntity.shopId)))
        .where(waitingEntity.waitingId.eq(waitingId))
        .fetchOne();
  }

  public List<WaitingDto> getWaitingByWaitingSeq(List<Long> waitingSeqs) {
    return queryFactory
        .select(fields(
            WaitingDto.class,
            waitingEntity.seq,
            waitingEntity.waitingId,
            waitingEntity.shopId,
            waitingEntity.registerChannel,
            waitingEntity.operationDate,
            waitingEntity.customerSeq,
            customerEntity.encCustomerPhone.as("customerPhoneNumber"),
            waitingEntity.customerName,
            shopCustomerEntity.sittingCount,
            waitingEntity.waitingNumbers.waitingNumber,
            waitingEntity.waitingNumbers.waitingOrder,
            waitingEntity.waitingStatus,
            waitingEntity.waitingDetailStatus,
            waitingEntity.seatOptionName,
            waitingEntity.totalPersonCount.as("totalSeatCount"),
            waitingEntity.personOptionsData.as("personOptions"),
            waitingEntity.expectedSittingDateTime,
            waitingEntity.waitingCompleteDateTime,
            waitingEntity.regDateTime
        ))
        .from(waitingEntity)
        .leftJoin(customerEntity).on(waitingEntity.customerSeq.eq(customerEntity.seq))
        .leftJoin(shopCustomerEntity)
        .on(customerEntity.seq.eq(shopCustomerEntity.shopCustomerId.customerSeq)
            .and(shopCustomerEntity.shopCustomerId.shopId.eq(waitingEntity.shopId))
        )
        .where(waitingEntity.seq.in(waitingSeqs))
        .fetch();
  }

  public List<WaitingCurrentStatusCountDto> findCurrentWaitingStatuses(List<String> shopIds,
      LocalDate operationDate) {
    return queryFactory
        .select(fields(WaitingCurrentStatusCountDto.class,
            waitingEntity.seq,
            waitingEntity.shopId,
            waitingEntity.totalPersonCount,
            waitingEntity.seatOptionName
        ))
        .from(waitingEntity)
        .where(
            waitingEntity.shopId.in(shopIds),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(WaitingStatus.WAITING)
        )
        .fetch();
  }

}
