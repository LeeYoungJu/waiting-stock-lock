package co.wadcorp.waiting.data.service.settings;

import co.wadcorp.waiting.data.domain.settings.DefaultOptionSettingDataFactory;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OptionSettingsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionSettingsService {

  private final OptionSettingsRepository optionSettingsRepository;

  public OptionSettingsEntity getOptionSettings(String shopId) {
    return optionSettingsRepository.findFirstByShopIdAndIsPublished(shopId, true)
        .orElseGet(() ->
            createDefaultOptionSettings(shopId)
        );
  }

  public OptionSettingsEntity save(OptionSettingsEntity entity) {
    optionSettingsRepository.findAllByShopIdInAndIsPublished(List.of(entity.getShopId()), true)
        .forEach(OptionSettingsEntity::unPublish);

    return optionSettingsRepository.save(entity);
  }

  private OptionSettingsEntity createDefaultOptionSettings(String shopId) {
    return new OptionSettingsEntity(shopId, DefaultOptionSettingDataFactory.create());
  }
}
