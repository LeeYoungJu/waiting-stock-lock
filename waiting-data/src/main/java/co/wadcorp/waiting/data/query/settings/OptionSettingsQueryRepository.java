package co.wadcorp.waiting.data.query.settings;

import static co.wadcorp.waiting.data.domain.settings.QOptionSettingsEntity.optionSettingsEntity;

import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class OptionSettingsQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<OptionSettingsEntity> findByShopIds(List<String> shopIds) {
    return queryFactory
        .selectFrom(optionSettingsEntity)
        .where(
            optionSettingsEntity.shopId.in(shopIds),
            optionSettingsEntity.isPublished.isTrue()
        )
        .fetch();
  }
}
