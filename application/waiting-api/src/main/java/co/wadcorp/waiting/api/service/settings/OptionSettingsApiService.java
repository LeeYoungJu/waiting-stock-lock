package co.wadcorp.waiting.api.service.settings;

import co.wadcorp.waiting.api.model.settings.request.OptionSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.OptionSettingsResponse;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.event.settings.ChangedOptionSettingsEvent;
import co.wadcorp.waiting.data.service.settings.OptionSettingsService;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptionSettingsApiService {

  private final OptionSettingsService optionSettingsService;
  private final WaitingService waitingService;
  private final ShopOperationInfoService shopOperationInfoService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public OptionSettingsResponse getWaitingOptionSettings(String shopId, LocalDate operationDate,
      ZonedDateTime nowZonedDateTime) {

    OptionSettingsEntity entity = optionSettingsService.getOptionSettings(shopId);
    ShopOperationInfoEntity shopOperationInfo = shopOperationInfoService.findByShopIdAndOperationDate(
        shopId, operationDate);
    boolean isOpen = isOpen(shopOperationInfo, nowZonedDateTime);
    boolean existsWaitingTeam = waitingService.existWaitingTeamByShopId(shopId, operationDate);

    return OptionSettingsResponse.toDto(entity, existsWaitingTeam, isOpen);

  }

  @Transactional
  public OptionSettingsResponse save(String shopId, String deviceId, OptionSettingsRequest request,
      LocalDate operationDate, ZonedDateTime nowZonedDateTime) {

    OptionSettingsEntity entity = request.toEntity(shopId);

    OptionSettingsEntity optionSettings = optionSettingsService.save(entity);

    ShopOperationInfoEntity shopOperationInfo = shopOperationInfoService.findByShopIdAndOperationDate(
        shopId, operationDate);
    boolean isOpen = isOpen(shopOperationInfo, nowZonedDateTime);
    boolean existsWaitingTeam = waitingService.validWaitingTeamExists(shopId, operationDate);

    eventPublisher.publishEvent(new ChangedOptionSettingsEvent(shopId, deviceId));

    return OptionSettingsResponse.toDto(optionSettings, existsWaitingTeam, isOpen);
  }

  private static boolean isOpen(ShopOperationInfoEntity shopOperationInfo,
      ZonedDateTime nowZonedDateTime) {

    return OperationStatus.OPEN == OperationStatus.find(shopOperationInfo, nowZonedDateTime);
  }
}
