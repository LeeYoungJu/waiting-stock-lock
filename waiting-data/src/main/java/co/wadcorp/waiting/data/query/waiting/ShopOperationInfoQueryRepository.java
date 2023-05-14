package co.wadcorp.waiting.data.query.waiting;

import static co.wadcorp.waiting.data.domain.shop.operation.QShopOperationInfoEntity.shopOperationInfoEntity;

import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.query.waiting.dto.QShopOperationInfoDto;
import co.wadcorp.waiting.data.query.waiting.dto.ShopOperationInfoDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class ShopOperationInfoQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<ShopOperationInfoEntity> findByShopIdsAndOperationDate(List<String> shopIds,
      LocalDate operationDate) {
    return queryFactory
        .selectFrom(shopOperationInfoEntity)
        .where(
            shopOperationInfoEntity.shopId.in(shopIds),
            shopOperationInfoEntity.operationDate.eq(operationDate)
        )
        .fetch();
  }

  public ShopOperationInfoDto selectShopOperationInfo(String shopId, LocalDate operationDate) {
    return queryFactory.select(
            new QShopOperationInfoDto(
                shopOperationInfoEntity.operationDate,
                shopOperationInfoEntity.registrableStatus,
                shopOperationInfoEntity.operationStartDateTime,
                shopOperationInfoEntity.operationEndDateTime,
                shopOperationInfoEntity.manualPauseInfo.manualPauseStartDateTime,
                shopOperationInfoEntity.manualPauseInfo.manualPauseEndDateTime,
                shopOperationInfoEntity.manualPauseInfo.manualPauseReasonId,
                shopOperationInfoEntity.manualPauseInfo.manualPauseReason,
                shopOperationInfoEntity.autoPauseInfo.autoPauseStartDateTime,
                shopOperationInfoEntity.autoPauseInfo.autoPauseEndDateTime,
                shopOperationInfoEntity.autoPauseInfo.autoPauseReasonId,
                shopOperationInfoEntity.autoPauseInfo.autoPauseReason,
                shopOperationInfoEntity.closedReason
            ))
        .from(shopOperationInfoEntity)
        .where(shopOperationInfoEntity.shopId.eq(shopId)
            .and(shopOperationInfoEntity.operationDate.eq(operationDate)))
        .fetchOne();
  }

}
