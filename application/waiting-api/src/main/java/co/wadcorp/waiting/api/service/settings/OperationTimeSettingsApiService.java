package co.wadcorp.waiting.api.service.settings;

import co.wadcorp.waiting.api.model.settings.request.OperationTimeSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.OperationTimeSettingsResponse;
import co.wadcorp.waiting.api.model.settings.vo.OperationTimeForDayVO;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.enums.OperationDay;
import co.wadcorp.waiting.data.event.settings.ChangedOperationTimeSettingsEvent;
import co.wadcorp.waiting.data.service.settings.OperationTimeSettingsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OperationTimeSettingsApiService {

  private final OperationTimeSettingsService operationTimeSettingsService;
  private final ApplicationEventPublisher eventPublisher;

  public OperationTimeSettingsResponse getOperationTimeSettings(String shopId) {
    OperationTimeSettingsEntity operationTimeSettings = operationTimeSettingsService.getOperationTimeSettings(
        shopId);
    return OperationTimeSettingsResponse.toDto(operationTimeSettings);
  }

  public OperationTimeSettingsResponse saveOperationTimeSettings(String shopId, String deviceId,
      OperationTimeSettingsRequest request) {

    OperationTimeSettingsEntity requestToEntity = request.toEntity(shopId);
    validateOperationTimeForDay(request.getOperationTimeForDays());

    OperationTimeSettingsEntity operationTimeSettings = operationTimeSettingsService.saveOperationTimeSettings(
        requestToEntity);

    eventPublisher.publishEvent(new ChangedOperationTimeSettingsEvent(shopId, deviceId));

    return OperationTimeSettingsResponse.toDto(operationTimeSettings);
  }

  private void validateOperationTimeForDay(List<OperationTimeForDayVO> operationTimeForDays) {
    operationTimeForDays.forEach(e -> OperationDay.find(e.getDay()));
  }
}
