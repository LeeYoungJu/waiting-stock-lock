package co.wadcorp.waiting.data.query.settings;

import static co.wadcorp.waiting.data.domain.settings.putoff.QDisablePutOffEntity.disablePutOffEntity;

import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class DisablePutOffQueryRepository {

  private final JPAQueryFactory queryFactory;

  public boolean isShopDisabledPutOff(String shopId) {
    return queryFactory
        .select(disablePutOffEntity)
        .from(disablePutOffEntity)
        .where(
            disablePutOffEntity.shopId.eq(shopId),
            disablePutOffEntity.isPublished.isTrue()
        )
        .fetchFirst() != null;
  }

  public List<DisablePutOffEntity> findDisabledPutOffBy(List<String> shopIds) {
    return queryFactory
        .select(disablePutOffEntity)
        .from(disablePutOffEntity)
        .where(
            disablePutOffEntity.shopId.in(shopIds),
            disablePutOffEntity.isPublished.isTrue()
        )
        .fetch();
  }

}
