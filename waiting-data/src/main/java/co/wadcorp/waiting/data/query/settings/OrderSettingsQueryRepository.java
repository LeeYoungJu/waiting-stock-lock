package co.wadcorp.waiting.data.query.settings;

import static co.wadcorp.waiting.data.domain.settings.QOrderSettingsEntity.orderSettingsEntity;

import co.wadcorp.waiting.data.domain.settings.DefaultOrderSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class OrderSettingsQueryRepository {

  private final JPAQueryFactory queryFactory;

  public OrderSettingsData findDataByShopId(String shopId) {
    return queryFactory
        .select(orderSettingsEntity.orderSettingsData)
        .from(orderSettingsEntity)
        .where(
            orderSettingsEntity.shopId.eq(shopId),
            orderSettingsEntity.isPublished.isTrue()
        )
        .stream()
        .findFirst()
        .orElseGet(DefaultOrderSettingDataFactory::create);
  }

  public List<OrderSettingsEntity> findByShopIds(List<String> shopIds) {
    return queryFactory
        .select(orderSettingsEntity)
        .from(orderSettingsEntity)
        .where(
            orderSettingsEntity.shopId.in(shopIds),
            orderSettingsEntity.isPublished.isTrue()
        )
        .fetch();
  }

}
