package co.wadcorp.waiting.data.domain.settings;

import java.util.List;
import java.util.Optional;

public interface OptionSettingsRepository {
  Optional<OptionSettingsEntity> findFirstByShopIdAndIsPublished(String shopId, boolean isPublished);

  List<OptionSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds, Boolean isPublished);

  OptionSettingsEntity save(OptionSettingsEntity entity);

  <S extends OptionSettingsEntity> List<S> saveAll(Iterable<S> entities);
}
