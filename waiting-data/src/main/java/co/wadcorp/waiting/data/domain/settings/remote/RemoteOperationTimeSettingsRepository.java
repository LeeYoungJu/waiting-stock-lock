package co.wadcorp.waiting.data.domain.settings.remote;

import java.util.List;

public interface RemoteOperationTimeSettingsRepository {

  RemoteOperationTimeSettingsEntity save(
      RemoteOperationTimeSettingsEntity remoteOperationTimeSettings);

  List<RemoteOperationTimeSettingsEntity> findAllByShopIdAndIsPublished(String shopId,
      boolean isPublished);

  List<RemoteOperationTimeSettingsEntity> findAll();

  <S extends RemoteOperationTimeSettingsEntity> List<S> saveAll(Iterable<S> entities);

  void deleteAllInBatch();

}
