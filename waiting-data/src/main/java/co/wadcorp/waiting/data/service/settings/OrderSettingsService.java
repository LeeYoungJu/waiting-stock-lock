package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.DefaultOptionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.DefaultOrderSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderSettingsService {

  private final OrderSettingsRepository orderSettingsRepository;

  public OrderSettingsEntity getOrderSettings(String shopId) {
    return orderSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true)
        .orElseGet(() -> createDefaultOptionSettings(shopId));
  }

  public OrderSettingsEntity saveOrderSettings(OrderSettingsEntity entity) {
    orderSettingsRepository.findByShopIdAndIsPublished(entity.getShopId(), true)
        .ifPresent(OrderSettingsEntity::unPublish);
    return orderSettingsRepository.save(entity);
  }

  private OrderSettingsEntity createDefaultOptionSettings(String shopId) {
    return OrderSettingsEntity.builder()
        .shopId(shopId)
        .orderSettingsData(DefaultOrderSettingDataFactory.create())
        .build();
  }

}
