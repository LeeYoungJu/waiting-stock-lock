package co.wadcorp.waiting.data.domain.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakePrecautionRepository implements PrecautionSettingsRepository {

  private final Map<String, PrecautionSettingsEntity> memoryMap = new HashMap<>();

  @Override
  public Optional<PrecautionSettingsEntity> findFirstByShopIdAndIsPublished(String shopId,
      Boolean isPublished) {
    return Optional.ofNullable(memoryMap.get(shopId));
  }

  @Override
  public List<PrecautionSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds,
      Boolean isPublished) {
    return memoryMap.values().stream()
        .filter(e -> shopIds.contains(e.getShopId()))
        .filter(e -> e.getIsPublished().equals(isPublished))
        .toList();
  }

  @Override
  public PrecautionSettingsEntity save(PrecautionSettingsEntity homeSettings) {
    String shopId = homeSettings.getShopId();
    memoryMap.put(shopId, homeSettings);
    return memoryMap.get(shopId);
  }

  @Override
  public <S extends PrecautionSettingsEntity> List<S> saveAll(Iterable<S> entities) {
    entities.forEach(item -> memoryMap.put(item.getShopId(), item));
    List<S> result = new ArrayList<>();
    entities.iterator().forEachRemaining(result::add);
    return result;
  }

}
