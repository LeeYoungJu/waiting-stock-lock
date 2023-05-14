package co.wadcorp.waiting.data.domain.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeShopRepository implements ShopRepository {

  private final Map<String, ShopEntity> memoryMap = new HashMap<>();

  @Override
  public ShopEntity save(ShopEntity shop) {
    return memoryMap.put(shop.getShopId(), shop);
  }

  @Override
  public <S extends ShopEntity> List<S> saveAll(Iterable<S> entities) {
    entities.forEach(item -> memoryMap.put(item.getShopId(), item));

    return null;
  }

  @Override
  public Optional<ShopEntity> findByShopId(String shopId) {
    return Optional.ofNullable(memoryMap.get(shopId));
  }

  @Override
  public List<ShopEntity> findAllByShopIdIn(List<String> shopIds) {
    return memoryMap.values().stream()
        .filter(item-> shopIds.contains(item.getShopId()))
        .toList();
  }

  @Override
  public void deleteAllInBatch() {
    memoryMap.clear();
  }

}
