package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.DefaultHomeSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeSettingsService {

  private final HomeSettingsRepository homeSettingsRepository;

  public HomeSettingsEntity getHomeSettings(String shopId) {
    return homeSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true)
        .orElseGet(() -> createDefaultHomeSettings(shopId));
  }

  public List<HomeSettingsEntity> findHomeSettings(List<String> shopIds) {
    return homeSettingsRepository.findAllByShopIdInAndIsPublished(shopIds, true);
  }

  public HomeSettingsEntity saveHomeSettings(HomeSettingsEntity entity) {
    homeSettingsRepository.findAllByShopIdInAndIsPublished(List.of(entity.getShopId()), true)
        .forEach(HomeSettingsEntity::unPublish);

    return homeSettingsRepository.save(entity);
  }

  private HomeSettingsEntity createDefaultHomeSettings(String shopId) {
    return new HomeSettingsEntity(shopId, DefaultHomeSettingDataFactory.create());
  }
}
