package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RemoteOperationTimeSettingsService {

  private final RemoteOperationTimeSettingsRepository remoteOperationTimeSettingsRepository;

  public List<RemoteOperationTimeSettingsEntity> getShopRemoteOperationTimeSettings(String shopId) {
    return remoteOperationTimeSettingsRepository.findAllByShopIdAndIsPublished(shopId, true);
  }

  public List<RemoteOperationTimeSettingsEntity> saveAll(
      List<RemoteOperationTimeSettingsEntity> remoteOperationTimeSettingsList) {
    return remoteOperationTimeSettingsRepository.saveAll(remoteOperationTimeSettingsList);
  }

}
