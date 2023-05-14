package co.wadcorp.waiting.data.query.settings;

import static co.wadcorp.waiting.data.domain.settings.QAlarmSettingsEntity.alarmSettingsEntity;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class AlarmSettingsQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<AlarmSettingsEntity> findAllPublishedByShopIds(List<String> shopIds) {
    return queryFactory
        .selectFrom(alarmSettingsEntity)
        .where(
            alarmSettingsEntity.shopId.in(shopIds),
            alarmSettingsEntity.isPublished.isTrue()
        )
        .fetch();
  }

}
