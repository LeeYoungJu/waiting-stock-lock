package co.wadcorp.waiting.data.domain.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeAlarmSettingsRepository implements AlarmSettingsRepository {

  private final Map<String, AlarmSettingsEntity> memoryMap = new HashMap<>();

  @Override
  public Optional<AlarmSettingsEntity> findFirstByShopIdAndIsPublished(String shopId,
      boolean isPublished) {

    return Optional.ofNullable(memoryMap.get(shopId));
  }

  @Override
  public List<AlarmSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds,
      Boolean isPublished) {
    return memoryMap.values().stream()
        .filter(e -> shopIds.contains(e.getShopId()))
        .filter(e -> e.getIsPublished().equals(isPublished))
        .toList();
  }

  @Override
  public AlarmSettingsEntity save(AlarmSettingsEntity alarmSettingsEntity) {
    String shopId = alarmSettingsEntity.getShopId();
    memoryMap.put(shopId, alarmSettingsEntity);
    return memoryMap.get(shopId);
  }

  @Override
  public <S extends AlarmSettingsEntity> List<S> saveAll(Iterable<S> entities) {
    entities.forEach(item -> memoryMap.put(item.getShopId(), item));
    List<S> result = new ArrayList<>();
    entities.iterator().forEachRemaining(result::add);
    return result;
  }
}
