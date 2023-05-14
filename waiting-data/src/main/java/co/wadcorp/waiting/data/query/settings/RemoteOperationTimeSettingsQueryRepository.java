package co.wadcorp.waiting.data.query.settings;

import static co.wadcorp.waiting.data.domain.settings.remote.QRemoteOperationTimeSettingsEntity.remoteOperationTimeSettingsEntity;

import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.ShopRemoteOperationTimeSettings;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class RemoteOperationTimeSettingsQueryRepository {

  private final JPAQueryFactory queryFactory;

  public ShopRemoteOperationTimeSettings getShopRemoteOperationTimeSettings(String shopId) {
    List<RemoteOperationTimeSettingsEntity> settings = queryFactory
        .selectFrom(remoteOperationTimeSettingsEntity)
        .where(
            remoteOperationTimeSettingsEntity.shopId.eq(shopId),
            remoteOperationTimeSettingsEntity.isPublished.isTrue()
        )
        .fetch();

    return ShopRemoteOperationTimeSettings.of(settings);
  }

}
