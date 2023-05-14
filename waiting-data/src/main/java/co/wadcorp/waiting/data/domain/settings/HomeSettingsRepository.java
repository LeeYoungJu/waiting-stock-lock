package co.wadcorp.waiting.data.domain.settings;

import java.util.List;
import java.util.Optional;

public interface HomeSettingsRepository {

  Optional<HomeSettingsEntity> findFirstByShopIdAndIsPublished(String shopId, Boolean isPublished);

  List<HomeSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds, Boolean isPublished);

  HomeSettingsEntity save(HomeSettingsEntity homeSettings);

  <S extends HomeSettingsEntity> List<S> saveAll(Iterable<S> entities);

  List<HomeSettingsEntity> findAll();

  void deleteAllInBatch();

}
