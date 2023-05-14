package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.waiting.QWaitingEntity.waitingEntity;

import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOrderCountDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class WaitingCountQueryRepository {

  private final JPAQueryFactory queryFactory;

  public int countAllWaitingTeamLessThanOrEqualOrder(String shopId, LocalDate operationDate,
      Integer waitingOrder, String seatOptionName) {
    return queryFactory
        .select(waitingEntity.seq)
        .from(waitingEntity)
        .where(
            seatOptionNameEq(seatOptionName),
            waitingEntity.shopId.eq(shopId)
                .and(waitingEntity.operationDate.eq(operationDate))
                .and(waitingEntity.waitingStatus.eq(WaitingStatus.WAITING))
                .and(waitingEntity.waitingNumbers.waitingOrder.loe(waitingOrder))
        )
        .fetch()
        .size();
  }

  public List<WaitingOrderCountDto> findAllWaitingOrdersShopIdsIn(List<String> shopIds,
      LocalDate operationDate) {
    return queryFactory
        .select(Projections.fields(
            WaitingOrderCountDto.class,
            waitingEntity.shopId,
            waitingEntity.waitingNumbers.waitingOrder,
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

  private BooleanExpression seatOptionNameEq(String seatOptionName) {
    if (StringUtils.isBlank(seatOptionName)) {
      return null;
    }
    return waitingEntity.seatOptionName.eq(seatOptionName);
  }
}
