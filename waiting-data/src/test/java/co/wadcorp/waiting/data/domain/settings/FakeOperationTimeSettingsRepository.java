package co.wadcorp.waiting.data.domain.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeOperationTimeSettingsRepository implements OperationTimeSettingsRepository {

  private final Map<String, OperationTimeSettingsEntity> memoryMap = new HashMap<>();

  @Override
  public Optional<OperationTimeSettingsEntity> findFirstByShopIdAndIsPublished(String shopId, Boolean isPublished) {
    return Optional.ofNullable(memoryMap.get(shopId));
  }

  @Override
  public Optional<OperationTimeSettingsEntity> findFirstByShopIdAndUnpublishedOrderBySeqDesc(
      String shopId) {
    return memoryMap.values().stream()
        .filter(e -> shopId.equals(e.getShopId()))
        .filter(e -> e.getIsPublished().equals(false))
        .sorted(Comparator.comparing(OperationTimeSettingsEntity::getSeq).reversed())
        .findFirst();
  }

  @Override
  public List<OperationTimeSettingsEntity> findAllByShopIdInAndIsPublished(List<String> shopIds,
      Boolean isPublished) {
    return memoryMap.values().stream()
        .filter(e -> shopIds.contains(e.getShopId()))
        .filter(e -> e.getIsPublished().equals(isPublished))
        .toList();  }

  @Override
  public OperationTimeSettingsEntity save(OperationTimeSettingsEntity operationTimeSettingsEntity) {
    String shopId = operationTimeSettingsEntity.getShopId();
    memoryMap.put(shopId, operationTimeSettingsEntity);
    return memoryMap.get(shopId);
  }

  @Override
  public void deleteAllInBatch() {
    memoryMap.clear();
  }

  @Override
  public <S extends OperationTimeSettingsEntity> List<S> saveAll(Iterable<S> entities) {
    entities.forEach(item -> memoryMap.put(item.getShopId(), item));
    List<S> result = new ArrayList<>();
    entities.iterator().forEachRemaining(result::add);
    return result;
  }

}
