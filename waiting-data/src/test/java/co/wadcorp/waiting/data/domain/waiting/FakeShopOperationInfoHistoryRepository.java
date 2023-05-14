package co.wadcorp.waiting.data.domain.waiting;

import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeShopOperationInfoHistoryRepository implements ShopOperationInfoHistoryRepository {

  private final Map<String, List<ShopOperationInfoHistoryEntity>> memoryMap = new HashMap<>();


  @Override
  public List<ShopOperationInfoHistoryEntity> findByShopIdAndOperationDate(String shopId,
      LocalDate operationDate) {

    List<ShopOperationInfoHistoryEntity> entities = memoryMap.getOrDefault(shopId, new ArrayList<>());

    return entities
        .stream()
        .filter(item -> operationDate.isEqual(item.getOperationDate()))
        .toList();
  }

  @Override
  public ShopOperationInfoHistoryEntity save(ShopOperationInfoHistoryEntity shopOperationInfoHistoryEntity) {
    String shopId = shopOperationInfoHistoryEntity.getShopId();

    List<ShopOperationInfoHistoryEntity> entities = memoryMap.getOrDefault(shopId, new ArrayList<>());
    entities.add(shopOperationInfoHistoryEntity);

    memoryMap.put(shopId, entities);
    return shopOperationInfoHistoryEntity;
  }

  @Override
  public List<ShopOperationInfoHistoryEntity> findAll() {
    return memoryMap.values().stream()
        .flatMap(Collection::stream)
        .toList();
  }

  @Override
  public void deleteAllInBatch() {
    memoryMap.clear();
  }

}
