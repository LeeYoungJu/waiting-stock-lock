package co.wadcorp.waiting.api.service.waiting;

import co.wadcorp.waiting.api.model.waiting.request.ChangeShopOperationStatusRequest;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.event.ShopOperationUpdatedEvent;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShopOperationApiService {

  private final ShopOperationInfoService shopOperationInfoService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void changeOperationStatus(String shopId, ChangeShopOperationStatusRequest request,
      ZonedDateTime nowLocalDateTime, String deviceId) {

    OperationStatus operationStatus = request.getOperationStatus();
    LocalDate operationDate = OperationDateUtils.getOperationDate(nowLocalDateTime.toLocalDateTime());

    if (operationStatus == OperationStatus.OPEN) {
      shopOperationInfoService.open(shopId, operationDate, nowLocalDateTime);
      eventPublisher.publishEvent(new ShopOperationUpdatedEvent(shopId, deviceId));
      return;
    }

    if (operationStatus == OperationStatus.CLOSED) {
      shopOperationInfoService.close(shopId, operationDate);
      eventPublisher.publishEvent(new ShopOperationUpdatedEvent(shopId, deviceId));
      return;
    }

    if (operationStatus == OperationStatus.BY_PASS) {
      shopOperationInfoService.byPass(shopId, operationDate);
      eventPublisher.publishEvent(new ShopOperationUpdatedEvent(shopId, deviceId));
      return;
    }

    if (operationStatus == OperationStatus.PAUSE) {
      shopOperationInfoService.pause(shopId, operationDate, request.getPauseReasonId(),
          request.getPausePeriod());
      eventPublisher.publishEvent(new ShopOperationUpdatedEvent(shopId, deviceId));
    }
  }

  public ShopOperationInfoEntity findByShopIdAndOperationDate(String waitingShopId, LocalDate operationDate) {
    return shopOperationInfoService.findByShopIdAndOperationDate(waitingShopId, operationDate);
  }
}
