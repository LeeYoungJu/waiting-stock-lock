package co.wadcorp.waiting.data.query.settings;

import static co.wadcorp.waiting.data.domain.settings.QHomeSettingsEntity.homeSettingsEntity;

import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class HomeSettingsQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<HomeSettingsEntity> findByShopIds(List<String> shopIds) {
    return queryFactory
        .selectFrom(homeSettingsEntity)
        .where(
            homeSettingsEntity.shopId.in(shopIds),
            homeSettingsEntity.isPublished.isTrue()
        )
        .fetch();
  }

  public HomeSettingsEntity findByShopId(String shopId) {
    return queryFactory
        .selectFrom(homeSettingsEntity)
        .where(
            homeSettingsEntity.shopId.eq(shopId),
            homeSettingsEntity.isPublished.isTrue()
        )
        .stream()
        .findFirst()
        .orElseGet(() -> new HomeSettingsEntity(shopId, DefaultHomeSettingDataFactory.create()));
  }

}
