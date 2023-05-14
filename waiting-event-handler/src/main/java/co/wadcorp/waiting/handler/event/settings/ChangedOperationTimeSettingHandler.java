package co.wadcorp.waiting.handler.event.settings;

import co.wadcorp.waiting.data.domain.settings.OperationTimeForDaysChangeChecker;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryRepository;
import co.wadcorp.waiting.data.event.ShopOperationUpdatedEvent;
import co.wadcorp.waiting.data.event.settings.ChangedOperationTimeSettingsEvent;
import co.wadcorp.waiting.data.service.settings.OperationTimeSettingsService;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ChangedOperationTimeSettingHandler {

  private final ShopOperationInfoService shopOperationInfoService;
  private final OperationTimeSettingsService operationTimeSettingsService;
  private final ShopOperationInfoHistoryRepository shopOperationInfoHistoryRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Async
  @TransactionalEventListener(ChangedOperationTimeSettingsEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void changeShopOperationInfo(ChangedOperationTimeSettingsEvent event) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    String shopId = event.shopId();

    OperationTimeSettingsEntity operationTimeSettings = operationTimeSettingsService.getOperationTimeSettings(
        shopId);

    List<ShopOperationInfoEntity> operationInfoEntities = shopOperationInfoService.findByShopIdAndOperationDateAfterOrEqual(
        shopId, operationDate);

    OperationTimeForDaysChangeChecker changeChecker = operationTimeSettingsService.isThereChangeInOperationTime(
        shopId);

    operationInfoEntities.forEach(operationInfo -> {
      operationInfo.updateOperationTimeSettings(operationTimeSettings, changeChecker);

      shopOperationInfoHistoryRepository.save(ShopOperationInfoHistoryEntity.of(operationInfo));
    });

    eventPublisher.publishEvent(new ShopOperationUpdatedEvent(shopId, event.deviceId()));
  }

}
