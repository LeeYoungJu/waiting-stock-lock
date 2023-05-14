package co.wadcorp.waiting.handler.event.settings;

import co.wadcorp.waiting.data.domain.settings.remote.ShopRemoteOperationTimeSettings;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryRepository;
import co.wadcorp.waiting.data.event.ShopOperationUpdatedEvent;
import co.wadcorp.waiting.data.event.settings.ChangedRemoteOperationTimeSettingsEvent;
import co.wadcorp.waiting.data.query.settings.RemoteOperationTimeSettingsQueryRepository;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
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
public class ChangedRemoteOperationTimeSettingHandler {

  private final ShopOperationInfoService shopOperationInfoService;
  private final RemoteOperationTimeSettingsQueryRepository remoteOperationTimeSettingsQueryRepository;
  private final ShopOperationInfoHistoryRepository shopOperationInfoHistoryRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Async
  @TransactionalEventListener(ChangedRemoteOperationTimeSettingsEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void changeShopOperationInfo(ChangedRemoteOperationTimeSettingsEvent event) {
    String shopId = event.shopId();

    ShopRemoteOperationTimeSettings shopRemoteOperationTimeSettings = remoteOperationTimeSettingsQueryRepository.getShopRemoteOperationTimeSettings(
        shopId);

    List<ShopOperationInfoEntity> operationInfoEntities = shopOperationInfoService.findByShopIdAndOperationDateAfterOrEqual(
        shopId, event.settingStartDate());

    operationInfoEntities.forEach(operationInfo -> {
      operationInfo.updateRemoteOperationTimeSettings(shopRemoteOperationTimeSettings);

      shopOperationInfoHistoryRepository.save(ShopOperationInfoHistoryEntity.of(operationInfo));
    });

    eventPublisher.publishEvent(new ShopOperationUpdatedEvent(shopId, "INTERNAL_API"));
  }

}
