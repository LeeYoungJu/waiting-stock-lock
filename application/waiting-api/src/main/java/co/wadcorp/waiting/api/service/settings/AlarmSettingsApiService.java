package co.wadcorp.waiting.api.service.settings;

import co.wadcorp.waiting.api.model.settings.request.AlarmSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.AlarmSettingsResponse;
import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.event.settings.ChangedAlarmSettingsEvent;
import co.wadcorp.waiting.data.service.settings.AlarmSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmSettingsApiService {

  private final AlarmSettingsService alarmSettingsService;
  private final ApplicationEventPublisher eventPublisher;

  public AlarmSettingsResponse getWaitingAlarmSettings(String shopId) {
    AlarmSettingsEntity entity = alarmSettingsService.getAlarmSettings(shopId);
    return AlarmSettingsResponse.toDto(entity);
  }

  public AlarmSettingsResponse save(String shopId, String deviceId, AlarmSettingsRequest request) {
    AlarmSettingsEntity entity = request.toEntity(shopId);

    AlarmSettingsEntity savedEntity = alarmSettingsService.save(entity);
    eventPublisher.publishEvent(new ChangedAlarmSettingsEvent(shopId, deviceId));
    return AlarmSettingsResponse.toDto(savedEntity);
  }
}
