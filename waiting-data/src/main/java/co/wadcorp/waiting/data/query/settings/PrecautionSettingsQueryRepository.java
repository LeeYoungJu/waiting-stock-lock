package co.wadcorp.waiting.data.query.settings;

import static co.wadcorp.waiting.data.domain.settings.QPrecautionSettingsEntity.precautionSettingsEntity;

import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class PrecautionSettingsQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<PrecautionSettingsEntity> findAllPublishedByShopIds(List<String> shopIds) {
    return queryFactory
        .selectFrom(precautionSettingsEntity)
        .where(
            precautionSettingsEntity.shopId.in(shopIds),
            precautionSettingsEntity.isPublished.isTrue()
        )
        .fetch();
  }

}
