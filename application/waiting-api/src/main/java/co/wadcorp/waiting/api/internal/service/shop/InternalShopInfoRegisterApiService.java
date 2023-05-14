package co.wadcorp.waiting.api.internal.service.shop;

import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalDisablePutOffSettingsServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalRemoteShopOperationTimeSettingsServiceRequest;
import co.wadcorp.waiting.data.domain.settings.putoff.DisablePutOffEntity;
import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.event.ShopOperationUpdatedEvent;
import co.wadcorp.waiting.data.event.settings.ChangedRemoteOperationTimeSettingsEvent;
import co.wadcorp.waiting.data.service.settings.DisablePutOffSettingsService;
import co.wadcorp.waiting.data.service.settings.RemoteOperationTimeSettingsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class InternalShopInfoRegisterApiService {

  private final RemoteOperationTimeSettingsService remoteOperationTimeSettingsService;
  private final DisablePutOffSettingsService disablePutOffSettingsService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void setRemoteOperationTimeSettings(String shopId,
      InternalRemoteShopOperationTimeSettingsServiceRequest request) {
    List<RemoteOperationTimeSettingsEntity> remoteOperationTimeSettings = remoteOperationTimeSettingsService.getShopRemoteOperationTimeSettings(
        shopId);
    remoteOperationTimeSettings.forEach(RemoteOperationTimeSettingsEntity::unPublish);

    List<RemoteOperationTimeSettingsEntity> remoteTimeSettings = request.toEntities(shopId);
    remoteOperationTimeSettingsService.saveAll(remoteTimeSettings);

    eventPublisher.publishEvent(
        new ChangedRemoteOperationTimeSettingsEvent(shopId, request.getSettingStartDate())
    );
  }

  @Transactional
  public void setDisablePutOff(String shopId, InternalDisablePutOffSettingsServiceRequest request) {
    List<DisablePutOffEntity> disablePutOffEntities = disablePutOffSettingsService.getDisablePutOff(
        shopId);
    disablePutOffEntities.forEach(DisablePutOffEntity::unPublish);

    if (request.isDisablePutOff()) {
      DisablePutOffEntity disablePutOffEntity = request.toEntity(shopId);
      disablePutOffSettingsService.save(disablePutOffEntity);
    }

    eventPublisher.publishEvent(new ShopOperationUpdatedEvent(shopId, "INTERNAL_API"));
  }

}
