package co.wadcorp.waiting.api.service.settings;

import co.wadcorp.waiting.api.model.settings.request.PrecautionSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.PrecautionSettingsResponse;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.event.settings.ChangedPrecationSettingsEvent;
import co.wadcorp.waiting.data.service.settings.PrecautionSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PrecautionSettingsApiService {

  private final PrecautionSettingsService precautionSettingsService;
  private final ApplicationEventPublisher eventPublisher;

  public PrecautionSettingsResponse getPrecautionSettings(String shopId) {
    PrecautionSettingsEntity precautionSettings = precautionSettingsService.getPrecautionSettings(
        shopId);

    return PrecautionSettingsResponse.toDto(precautionSettings);
  }

  @Transactional
  public PrecautionSettingsResponse savePrecautionSettings(String shopId,
      String deviceId, PrecautionSettingsRequest precautionSettingsRequest) {
    PrecautionSettingsEntity precautionSettingsEntity = precautionSettingsService.savePrecautionSettings(
        precautionSettingsRequest.toEntity(shopId));

    eventPublisher.publishEvent(new ChangedPrecationSettingsEvent(shopId, deviceId));

    return PrecautionSettingsResponse.toDto(precautionSettingsEntity);
  }
}
