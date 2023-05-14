package co.wadcorp.waiting.data.domain.waiting;

import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeShopOperationInfoRepository implements ShopOperationInfoRepository {

  private final Map<String, List<ShopOperationInfoEntity>> memoryMap = new HashMap<>();


  @Override
  public Optional<ShopOperationInfoEntity> findByShopIdAndOperationDate(String shopId,
      LocalDate operationDate) {

    List<ShopOperationInfoEntity> entities = memoryMap.getOrDefault(shopId, new ArrayList<>());

    return entities
        .stream()
        .filter(item -> operationDate.isEqual(item.getOperationDate()))
        .findFirst();
  }

  @Override
  public List<ShopOperationInfoEntity> findByShopIdAndOperationDateAfterOrEqual(String shopId,
      LocalDate operationDate) {
    List<ShopOperationInfoEntity> entities = memoryMap.getOrDefault(shopId, new ArrayList<>());

    return entities.stream()
        .filter(item -> !operationDate.isAfter(item.getOperationDate()))
        .toList();
  }

  @Override
  public List<ShopOperationInfoEntity> findAllByShopIdInAndOperationDate(List<String> shopIds,
      LocalDate operationDate) {
    List<ShopOperationInfoEntity> results = new ArrayList<>(List.of());
    for (String shopId : shopIds) {
      List<ShopOperationInfoEntity> entities = memoryMap.getOrDefault(shopId, new ArrayList<>());

      results.addAll(entities.stream()
          .filter(item -> operationDate.isEqual(item.getOperationDate()))
          .toList()
      );
    }
    return results;
  }

  @Override
  public ShopOperationInfoEntity save(ShopOperationInfoEntity shopOperationInfoEntity) {

    String shopId = shopOperationInfoEntity.getShopId();

    List<ShopOperationInfoEntity> entities = memoryMap.getOrDefault(shopId, new ArrayList<>());
    entities.add(shopOperationInfoEntity);

    memoryMap.put(shopId, entities);
    return shopOperationInfoEntity;
  }

  @Override
  public <S extends ShopOperationInfoEntity> List<S> saveAll(Iterable<S> entities) {
    List<S> results = new ArrayList<>();
    for (S entity : entities) {
      save(entity);
      results.add(entity);
    }
    return results;
  }

  @Override
  public List<ShopOperationInfoEntity> findAll() {
    return memoryMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
  }

  @Override
  public void deleteAllInBatch() {
    memoryMap.clear();
  }

}
