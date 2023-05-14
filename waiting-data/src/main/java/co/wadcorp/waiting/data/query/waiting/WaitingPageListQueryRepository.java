package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.customer.QCustomerEntity.customerEntity;
import static co.wadcorp.waiting.data.domain.customer.QShopCustomerEntity.shopCustomerEntity;
import static co.wadcorp.waiting.data.domain.memo.QWaitingMemoEntity.waitingMemoEntity;
import static co.wadcorp.waiting.data.domain.shop.QShopEntity.shopEntity;
import static co.wadcorp.waiting.data.domain.shop.operation.QShopOperationInfoEntity.shopOperationInfoEntity;
import static co.wadcorp.waiting.data.domain.waiting.QWaitingEntity.waitingEntity;
import static com.querydsl.core.types.Projections.fields;

import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOnRegistrationDto;
import co.wadcorp.waiting.data.query.waiting.dto.WebWaitingDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class WaitingPageListQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Page<WaitingDto> getDefaultWaitingList(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, Pageable pageable) {

    // 웨이팅 카운트 조회
    Long count = queryFactory
        .select(waitingEntity.count())
        .from(waitingEntity)
        .where(
            waitingEntity.shopId.eq(shopId),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(waitingStatus)
        )
        .fetchOne();

    if (count == null || count == 0) {
      return PageableExecutionUtils.getPage(List.of(), pageable, () -> 0L);
    }

    // 웨이팅 정보 조회
    List<WaitingDto> waitingDtos = queryFactory
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
            waitingMemoEntity.memo.coalesce("").as("waitingMemo"),
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
            .and(shopCustomerEntity.shopCustomerId.shopId.eq(shopId))
        )
        .leftJoin(waitingMemoEntity).on(waitingEntity.waitingId.eq(waitingMemoEntity.waitingId))
        .where(
            waitingEntity.shopId.eq(shopId),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(waitingStatus)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(orderBy(waitingStatus))
        .fetch();

    return PageableExecutionUtils.getPage(waitingDtos, pageable, () -> count);
  }

  public Page<WaitingDto> getTableWaitingList(String shopId, LocalDate operationDate,
      WaitingStatus waitingStatus, List<String> seatOptionName, Pageable pageable) {
    Long count = queryFactory
        .select(waitingEntity.count())
        .from(waitingEntity)
        .where(
            waitingEntity.shopId.eq(shopId),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(waitingStatus),
            seatOptionNameIn(seatOptionName)
        )
        .fetchOne();

    if (count == null || count == 0) {
      return PageableExecutionUtils.getPage(List.of(), pageable, () -> 0L);
    }

    List<WaitingDto> waitingDtos = queryFactory
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
            waitingMemoEntity.memo.coalesce("").as("waitingMemo"),
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
            .and(shopCustomerEntity.shopCustomerId.shopId.eq(shopId))
        )
        .leftJoin(waitingMemoEntity).on(waitingEntity.waitingId.eq(waitingMemoEntity.waitingId))
        .where(
            waitingEntity.shopId.eq(shopId),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(waitingStatus),
            seatOptionNameIn(seatOptionName)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(orderBy(waitingStatus))
        .fetch();

    return PageableExecutionUtils.getPage(waitingDtos, pageable, () -> count);
  }

  public Page<WaitingOnRegistrationDto> getWaitingListOnRegistration(String shopId,
      LocalDate operationDate, Pageable pageable) {
    Long count = queryFactory
        .select(waitingEntity.seq.count())
        .from(waitingEntity)
        .where(
            waitingEntity.shopId.eq(shopId),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(WaitingStatus.WAITING)
        )
        .fetchOne();

    if (count == null || count == 0) {
      return PageableExecutionUtils.getPage(List.of(), pageable, () -> 0L);
    }

    List<WaitingOnRegistrationDto> waitingDtos = queryFactory
        .select(fields(
            WaitingOnRegistrationDto.class,
            waitingEntity.seq,
            waitingEntity.waitingNumbers.waitingNumber,
            waitingEntity.seatOptionName,
            waitingEntity.totalPersonCount,
            waitingEntity.regDateTime
        ))
        .from(waitingEntity)
        .where(
            waitingEntity.shopId.eq(shopId),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(WaitingStatus.WAITING)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(
            waitingEntity.waitingNumbers.waitingOrder.asc()
        )
        .fetch();

    return PageableExecutionUtils.getPage(waitingDtos, pageable, () -> count);
  }

  public List<WebWaitingDto> getAllWaitingByCustomer(Long customerSeq, LocalDate operationDate) {
    return queryFactory.select(fields(
            WebWaitingDto.class,
            waitingEntity.waitingId,
            waitingEntity.shopId,
            shopEntity.shopName,
            waitingEntity.waitingNumbers.waitingOrder,
            waitingEntity.seatOptionName,
            waitingEntity.regDateTime
        ))
        .from(waitingEntity)
        .innerJoin(shopOperationInfoEntity)
        .on(waitingEntity.shopId.eq(shopOperationInfoEntity.shopId)
            .and(shopOperationInfoEntity.operationDate.eq(operationDate)))
        .innerJoin(shopEntity).on(shopEntity.shopId.eq(waitingEntity.shopId))
        .where(
            waitingEntity.customerSeq.eq(customerSeq),
            waitingEntity.operationDate.eq(operationDate),
            waitingEntity.waitingStatus.eq(WaitingStatus.WAITING)
        )
        .fetch();
  }

  private static BooleanExpression seatOptionNameIn(List<String> seatOptionName) {
    if (seatOptionName == null || seatOptionName.isEmpty()) {
      return null;
    }

    return waitingEntity.seatOptionName.in(seatOptionName);
  }

  private static OrderSpecifier<?> orderBy(WaitingStatus waitingStatus) {
    if (waitingStatus == WaitingStatus.WAITING) {
      return waitingEntity.waitingNumbers.waitingOrder.asc();
    }
    if (waitingStatus == WaitingStatus.SITTING || waitingStatus == WaitingStatus.CANCEL) {
      return waitingEntity.waitingCompleteDateTime.desc();
    }
    return null;
  }

}
