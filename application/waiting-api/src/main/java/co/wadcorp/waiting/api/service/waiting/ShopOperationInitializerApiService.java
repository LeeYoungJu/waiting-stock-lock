package co.wadcorp.waiting.api.service.waiting;

import co.wadcorp.waiting.api.model.waiting.response.ShopOperationInitializerResponse;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInitializeFactory;
import co.wadcorp.waiting.data.service.settings.OperationTimeSettingsService;
import co.wadcorp.waiting.data.service.waiting.ShopOperationInfoService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShopOperationInitializerApiService {

  private final ShopOperationInfoService shopOperationInfoService;
  private final OperationTimeSettingsService operationTimeSettingsService;

  @Transactional
  public ShopOperationInitializerResponse initializer(String shopId, LocalDate operationDate,
      ZonedDateTime nowDateTime) {

    ShopOperationInfoEntity operationInfoEntity = shopOperationInfoService.getByShopIdAndOperationDate(
        shopId, operationDate
    );

    // 이미 초기화된 데이터가 있으면 return
    if (operationInfoEntity != ShopOperationInfoEntity.EMPTY_OPERATION_INFO) {
      return ShopOperationInitializerResponse.toDto(operationInfoEntity, nowDateTime);
    }

    OperationTimeSettingsEntity operationTimeSettings = operationTimeSettingsService.getOperationTimeSettings(
        shopId
    );

    ShopOperationInfoEntity shopOperationInfoEntity = ShopOperationInitializeFactory.initialize(
        operationTimeSettings, operationDate
    );

    ShopOperationInfoEntity save = shopOperationInfoService.save(shopOperationInfoEntity);
    return ShopOperationInitializerResponse.toDto(save, nowDateTime);
  }
}
