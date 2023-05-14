package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.customer.QCustomerEntity.customerEntity;
import static co.wadcorp.waiting.data.domain.shop.QShopEntity.shopEntity;
import static co.wadcorp.waiting.data.domain.shop.operation.QShopOperationInfoEntity.shopOperationInfoEntity;
import static co.wadcorp.waiting.data.domain.waiting.QWaitingEntity.waitingEntity;

import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.MyWaitingInfoDto;
import co.wadcorp.waiting.data.query.waiting.dto.QMyWaitingInfoDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOfOtherShopQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
public class WaitingRegisterQueryRepository {

  private final JPAQueryFactory queryFactory;

  public WaitingRegisterQueryRepository(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  public List<WaitingOfOtherShopQueryDto> getAllWaitingOfOtherShopByCustomerPhone(
      PhoneNumber encCustomerPhone, LocalDate operationDate) {
    return queryFactory.select(Projections.fields(
            WaitingOfOtherShopQueryDto.class,
            waitingEntity.waitingId,
            waitingEntity.shopId,
            shopEntity.shopName,
            waitingEntity.customerSeq,
            waitingEntity.waitingNumbers.waitingOrder,
            waitingEntity.seatOptionName,
            waitingEntity.regDateTime))
        .from(waitingEntity)
        .innerJoin(shopOperationInfoEntity)
        .on(waitingEntity.shopId.eq(shopOperationInfoEntity.shopId)
            .and(shopOperationInfoEntity.operationDate.eq(operationDate)))
        .innerJoin(shopEntity).on(shopEntity.shopId.eq(waitingEntity.shopId))
        .innerJoin(customerEntity).on(waitingEntity.customerSeq.eq(customerEntity.seq))
        .where(
            customerEntity.encCustomerPhone.eq(encCustomerPhone)
                .and(waitingEntity.operationDate.eq(operationDate))
                .and(waitingEntity.waitingStatus.eq(WaitingStatus.WAITING))
        )
        .fetch();
  }


  public Optional<MyWaitingInfoDto> getWaitingByShopIdAndCustomerSeqToday(String shopId,
      LocalDate operationDate, Long customerSeq) {
    return Optional.ofNullable(
        queryFactory.select(new QMyWaitingInfoDto(
                waitingEntity.waitingNumbers.waitingNumber, customerEntity.encCustomerPhone,
                waitingEntity.totalPersonCount, waitingEntity.regDateTime))
            .from(waitingEntity)
            .innerJoin(customerEntity).on(waitingEntity.customerSeq.eq(customerEntity.seq))
            .where(
                waitingEntity.shopId.eq(shopId)
                    .and(waitingEntity.customerSeq.eq(customerSeq))
                    .and(waitingEntity.operationDate.eq(operationDate))
                    .and(waitingEntity.waitingStatus.eq(WaitingStatus.WAITING))
            )
            .fetchFirst());
  }
}
