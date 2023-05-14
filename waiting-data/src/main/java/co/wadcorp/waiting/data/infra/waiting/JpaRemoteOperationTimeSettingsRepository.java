package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRemoteOperationTimeSettingsRepository extends
    RemoteOperationTimeSettingsRepository, JpaRepository<RemoteOperationTimeSettingsEntity, Long> {

  List<RemoteOperationTimeSettingsEntity> findAllByShopIdAndIsPublished(String shopId,
      boolean isPublished);

}
