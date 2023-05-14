package co.wadcorp.waiting.data.query.shop;

import static co.wadcorp.waiting.data.domain.shop.QShopEntity.shopEntity;
import static com.querydsl.core.types.Projections.fields;

import co.wadcorp.waiting.data.query.shop.dto.QShopDto;
import co.wadcorp.waiting.data.query.shop.dto.ShopDto;
import co.wadcorp.waiting.data.query.shop.dto.ShopSeqShopIdDto;
import co.wadcorp.waiting.data.query.shop.dto.ShopUsedFieldDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class ShopQueryRepository {

  private final JPAQueryFactory queryFactory;

  public ShopDto getByShopId(String shopId) {
    return queryFactory.select(
            new QShopDto(
                shopEntity.shopId,
                shopEntity.shopName,
                shopEntity.shopAddress,
                shopEntity.shopTelNumber
            )
        ).from(shopEntity)
        .where(shopEntity.shopId.eq(shopId))
        .fetchOne();
  }

  public List<ShopDto> findByShopIds(List<String> shopIds) {
    return queryFactory
        .select(new QShopDto(
            shopEntity.shopId,
            shopEntity.shopName,
            shopEntity.shopAddress,
            shopEntity.shopTelNumber
        ))
        .from(shopEntity)
        .where(shopEntity.shopId.in(shopIds))
        .fetch();
  }

  public List<ShopUsedFieldDto> findByUsedWaitings(List<String> shopIds) {
    return queryFactory
        .select(fields(
            ShopUsedFieldDto.class,
            shopEntity.shopId,
            shopEntity.isUsedRemoteWaiting,
            shopEntity.isMembership
        ))
        .from(shopEntity)
        .where(shopEntity.shopId.in(shopIds))
        .fetch();
  }

  public List<ShopSeqShopIdDto> findShopSeqsByNoOffsetPaging(Long minSeq, int limit) {
    return queryFactory
        .select(fields(
            ShopSeqShopIdDto.class,
            shopEntity.seq,
            shopEntity.shopId
        ))
        .from(shopEntity)
        .where(
            shopEntity.seq.goe(minSeq)
        )
        .orderBy(shopEntity.seq.asc())
        .limit(limit)
        .fetch();
  }

  public List<String> findAllShopIds() {
    return queryFactory
        .select(shopEntity.shopId)
        .from(shopEntity)
        .fetch();
  }

}
