package co.wadcorp.waiting.data.domain.settings;

import java.util.List;
import java.util.Optional;

public interface OrderSettingsRepository {

  OrderSettingsEntity save(OrderSettingsEntity orderSettingsEntity);

  Optional<OrderSettingsEntity> findFirstByShopIdAndIsPublished(String shopId, boolean isPublished);

  Optional<OrderSettingsEntity> findByShopIdAndIsPublished(String shopId, boolean isPublished);

  List<OrderSettingsEntity> findAll();

}
