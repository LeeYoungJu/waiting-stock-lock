package co.wadcorp.waiting.data.domain.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeHomeSettingsRepository implements HomeSettingsRepository {

  private final Map<String, HomeSettingsEntity> memoryMap = new HashMap<>();

  @Override
  public Optional<HomeSettingsEntity> findFirstByShopIdAndIsPublished(String shopId,
      Boolean isPublished) {
    return Optional.ofNullable(memoryMap.get(shopId));
  }

  @Override
  public List<HomeSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds,
      Boolean isPublished) {
    return memoryMap.values().stream()
        .filter(e -> shopIds.contains(e.getShopId()))
        .filter(e -> e.getIsPublished().equals(isPublished))
        .toList();
  }

  @Override
  public HomeSettingsEntity save(HomeSettingsEntity homeSettings) {
    String shopId = homeSettings.getShopId();
    memoryMap.put(shopId, homeSettings);
    return memoryMap.get(shopId);
  }

  @Override
  public <S extends HomeSettingsEntity> List<S> saveAll(Iterable<S> entities) {
    entities.forEach(item -> memoryMap.put(item.getShopId(), item));
    List<S> result = new ArrayList<>();
    entities.iterator().forEachRemaining(result::add);
    return result;
  }

  @Override
  public List<HomeSettingsEntity> findAll() {
    return memoryMap.values().stream()
        .toList();
  }

  @Override
  public void deleteAllInBatch() {
    memoryMap.clear();
  }

}
