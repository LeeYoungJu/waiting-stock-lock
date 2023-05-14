package co.wadcorp.waiting.data.domain.settings;

import java.util.List;
import java.util.Optional;

public interface AlarmSettingsRepository {
  Optional<AlarmSettingsEntity> findFirstByShopIdAndIsPublished(String shopId, boolean isPublished);

  List<AlarmSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds, Boolean isPublished);

  AlarmSettingsEntity save(AlarmSettingsEntity alarmSettingsEntity);

  <S extends AlarmSettingsEntity> List<S> saveAll(Iterable<S> entities);
}
