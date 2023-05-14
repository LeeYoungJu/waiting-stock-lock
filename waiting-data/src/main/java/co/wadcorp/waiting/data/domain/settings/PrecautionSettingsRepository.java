package co.wadcorp.waiting.data.domain.settings;

import java.util.List;
import java.util.Optional;

public interface PrecautionSettingsRepository {

  Optional<PrecautionSettingsEntity> findFirstByShopIdAndIsPublished(String shopId, Boolean isPublished);

  List<PrecautionSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds, Boolean isPublished);

  PrecautionSettingsEntity save(PrecautionSettingsEntity homeSettings);

  <S extends PrecautionSettingsEntity> List<S> saveAll(Iterable<S> entities);
}
