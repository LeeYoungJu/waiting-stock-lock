package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.customer.QCustomerEntity.customerEntity;
import static co.wadcorp.waiting.data.domain.customer.QShopCustomerEntity.shopCustomerEntity;
import static co.wadcorp.waiting.data.domain.waiting.QWaitingEntity.waitingEntity;
import static co.wadcorp.waiting.data.domain.waiting.QWaitingHistoryEntity.waitingHistoryEntity;
import static com.querydsl.core.types.Projections.fields;

import co.wadcorp.waiting.data.domain.customer.QCustomerEntity;
import co.wadcorp.waiting.data.domain.customer.QShopCustomerEntity;
import co.wadcorp.waiting.data.domain.waiting.QWaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.query.waiting.dto.QWaitingCalledHistoryDto;
import co.wadcorp.waiting.data.query.waiting.dto.QWaitingHistoriesDto_WaitingDto;
import co.wadcorp.waiting.data.query.waiting.dto.QWaitingHistoriesDto_WaitingHistoryDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCalledHistoryDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto.WaitingDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto.WaitingHistoryDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoryDetailStatusDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class WaitingHistoryQueryRepository {

  private final JPAQueryFactory queryFactory;

  public WaitingHistoriesDto getWaitingHistories(String shopId, String waitingId) {
    WaitingDto waitingHistoryDto = queryFactory.select(
            select(waitingEntity, customerEntity, shopCustomerEntity))
        .from(waitingEntity)
        .leftJoin(customerEntity).on(waitingEntity.customerSeq.eq(customerEntity.seq))
        .leftJoin(shopCustomerEntity)
        .on(customerEntity.seq.eq(shopCustomerEntity.shopCustomerId.customerSeq)
            .and(shopCustomerEntity.shopCustomerId.shopId.eq(shopId)))
        .where(waitingEntity.shopId.eq(shopId)
            .and(waitingEntity.waitingId.eq(waitingId)))
        .fetchOne();

    List<WaitingHistoryDto> waitingHistories = queryFactory.select(
            new QWaitingHistoriesDto_WaitingHistoryDto(
                waitingHistoryEntity.waitingStatus,
                waitingHistoryEntity.waitingDetailStatus,
                waitingHistoryEntity.regDateTime
            ))
        .from(waitingHistoryEntity)
        .where(waitingHistoryEntity.shopId.eq(shopId)
            .and(waitingHistoryEntity.waitingId.eq(waitingId)))
        .fetch();

    return new WaitingHistoriesDto(waitingHistoryDto, waitingHistories);
  }


  public WaitingHistoriesDto getWaitingHistories(String waitingId) {
    WaitingDto waitingHistoryDto = queryFactory.select(
            select(waitingEntity, customerEntity, shopCustomerEntity))
        .from(waitingEntity)
        .leftJoin(customerEntity).on(waitingEntity.customerSeq.eq(customerEntity.seq))
        .leftJoin(shopCustomerEntity)
        .on(customerEntity.seq.eq(shopCustomerEntity.shopCustomerId.customerSeq)
            .and(shopCustomerEntity.shopCustomerId.shopId.eq(waitingEntity.shopId)))
        .where(waitingEntity.waitingId.eq(waitingId))
        .fetchOne();

    List<WaitingHistoryDto> waitingHistories = queryFactory.select(
            new QWaitingHistoriesDto_WaitingHistoryDto(
                waitingHistoryEntity.waitingStatus,
                waitingHistoryEntity.waitingDetailStatus,
                waitingHistoryEntity.regDateTime
            ))
        .from(waitingHistoryEntity)
        .where(waitingHistoryEntity.waitingId.eq(waitingId))
        .fetch();

    return new WaitingHistoriesDto(waitingHistoryDto, waitingHistories);
  }

  public List<WaitingCalledHistoryDto> getWaitingCalledHistory(String waitingId) {
    return queryFactory.select(
            new QWaitingCalledHistoryDto(
                waitingHistoryEntity.waitingId,
                waitingHistoryEntity.regDateTime
            )
        )
        .from(waitingHistoryEntity)
        .where(waitingHistoryEntity.waitingId.eq(waitingId)
            .and(waitingHistoryEntity.waitingDetailStatus.eq(WaitingDetailStatus.CALL)))
        .fetch();
  }

  public List<WaitingCalledHistoryDto> getWaitingCalledHistory(List<String> waitingIds) {
    return queryFactory.select(
            new QWaitingCalledHistoryDto(
                waitingHistoryEntity.waitingId,
                waitingHistoryEntity.regDateTime
            )
        )
        .from(waitingHistoryEntity)
        .where(waitingHistoryEntity.waitingId.in(waitingIds)
            .and(waitingHistoryEntity.waitingDetailStatus.eq(WaitingDetailStatus.CALL)))
        .fetch();
  }

  public List<WaitingHistoryDetailStatusDto> findLastWaitingHistoryDetailStatuses(String waitingId,
      int size) {
    return queryFactory
        .select(fields(
            WaitingHistoryDetailStatusDto.class,
            waitingHistoryEntity.seq,
            waitingHistoryEntity.waitingId,
            waitingHistoryEntity.waitingStatus,
            waitingHistoryEntity.waitingDetailStatus
        ))
        .from(waitingHistoryEntity)
        .where(
            waitingHistoryEntity.waitingId.eq(waitingId)
        )
        .orderBy(waitingHistoryEntity.seq.desc())
        .limit(size)
        .fetch();
  }

  private static QWaitingHistoriesDto_WaitingDto select(QWaitingEntity waitingEntity,
      QCustomerEntity customerEntity, QShopCustomerEntity shopCustomerEntity) {
    return new QWaitingHistoriesDto_WaitingDto(
        waitingEntity.waitingId,
        waitingEntity.shopId,
        waitingEntity.registerChannel,
        waitingEntity.waitingStatus,
        waitingEntity.waitingDetailStatus,
        waitingEntity.operationDate,
        waitingEntity.waitingNumbers.waitingNumber,
        waitingEntity.waitingNumbers.waitingOrder,
        customerEntity.encCustomerPhone,
        waitingEntity.customerName,
        shopCustomerEntity.visitCount,
        waitingEntity.totalPersonCount,
        waitingEntity.personOptionsData,
        waitingEntity.seatOptionName,
        waitingEntity.regDateTime
    );
  }

}
