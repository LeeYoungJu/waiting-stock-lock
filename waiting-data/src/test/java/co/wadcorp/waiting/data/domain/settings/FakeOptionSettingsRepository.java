package co.wadcorp.waiting.data.domain.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeOptionSettingsRepository implements OptionSettingsRepository {

  private final Map<String, OptionSettingsEntity> memoryMap = new HashMap<>();

  @Override
  public Optional<OptionSettingsEntity> findFirstByShopIdAndIsPublished(String shopId,
      boolean isPublished) {

    return Optional.ofNullable(memoryMap.get(shopId));
  }

  @Override
  public List<OptionSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds,
      Boolean isPublished) {
    return memoryMap.values().stream()
        .filter(e -> shopIds.contains(e.getShopId()))
        .filter(e -> e.getIsPublished().equals(isPublished))
        .toList();
  }

  @Override
  public OptionSettingsEntity save(OptionSettingsEntity optionSettingsEntity) {
    String shopId = optionSettingsEntity.getShopId();
    memoryMap.put(shopId, optionSettingsEntity);
    return memoryMap.get(shopId);
  }

  @Override
  public <S extends OptionSettingsEntity> List<S> saveAll(Iterable<S> entities) {
    entities.forEach(item -> memoryMap.put(item.getShopId(), item));
    List<S> result = new ArrayList<>();
    entities.iterator().forEachRemaining(result::add);
    return result;
  }
}