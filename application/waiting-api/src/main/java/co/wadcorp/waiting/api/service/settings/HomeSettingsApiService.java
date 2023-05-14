package co.wadcorp.waiting.api.service.settings;

import co.wadcorp.waiting.api.model.settings.request.HomeSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.HomeSettingsResponse;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.event.TableUpdatedEvent;
import co.wadcorp.waiting.data.event.settings.ChangedHomeSettingsEvent;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeSettingsApiService {

  private final HomeSettingsService homeSettingsService;
  private final WaitingService waitingService;
  private final ShopOperationInfoService shopOperationInfoService;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public HomeSettingsResponse getHomeSettings(String shopId, LocalDate operationDate,
      ZonedDateTime nowZonedDateTime) {
    HomeSettingsEntity homeSettings = homeSettingsService.getHomeSettings(shopId);

    ShopOperationInfoEntity shopOperationInfo = shopOperationInfoService.findByShopIdAndOperationDate(
        shopId, operationDate);
    boolean isOpen = isOpen(shopOperationInfo, nowZonedDateTime);
    boolean existsWaitingTeam = waitingService.existWaitingTeamByShopId(shopId, operationDate);

    return HomeSettingsResponse.toDto(homeSettings.getHomeSettingsData(),
        existsWaitingTeam, isOpen);
  }

  @Transactional
  public HomeSettingsResponse saveHomeSettings(String shopId, String deviceId,
      HomeSettingsRequest request, LocalDate operationDate, ZonedDateTime nowZonedDateTime) {
    boolean existsWaitingTeam = waitingService.validWaitingTeamExists(shopId, operationDate);

    HomeSettingsEntity homeSettings = homeSettingsService.saveHomeSettings(
        request.toEntity(shopId));

    ShopOperationInfoEntity shopOperationInfo = shopOperationInfoService.findByShopIdAndOperationDate(
        shopId, operationDate);
    boolean isOpen = isOpen(shopOperationInfo, nowZonedDateTime);

    eventPublisher.publishEvent(new TableUpdatedEvent(shopId, operationDate));
    eventPublisher.publishEvent(new ChangedHomeSettingsEvent(shopId, deviceId));

    return HomeSettingsResponse.toDto(homeSettings.getHomeSettingsData(), existsWaitingTeam,
        isOpen);
  }

  private static boolean isOpen(ShopOperationInfoEntity shopOperationInfo,
      ZonedDateTime nowZonedDateTime) {

    return OperationStatus.OPEN == OperationStatus.find(shopOperationInfo, nowZonedDateTime);
  }
}
