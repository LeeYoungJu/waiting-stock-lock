package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.DefaultPrecautionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.PrecautionSettingsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PrecautionSettingsService {

  private final PrecautionSettingsRepository precautionSettingsRepository;

  public PrecautionSettingsEntity getPrecautionSettings(String shopId) {
    return precautionSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true)
        .orElseGet(
            () -> createDefaultPrecautionSettings(shopId)
        );
  }

  public PrecautionSettingsEntity savePrecautionSettings(PrecautionSettingsEntity entity) {
    precautionSettingsRepository.findAllByShopIdInAndIsPublished(List.of(entity.getShopId()), true)
        .forEach(PrecautionSettingsEntity::unPublish);

    return precautionSettingsRepository.save(entity);
  }

  private PrecautionSettingsEntity createDefaultPrecautionSettings(String shopId) {
    return new PrecautionSettingsEntity(shopId, DefaultPrecautionSettingDataFactory.create());
  }
}
