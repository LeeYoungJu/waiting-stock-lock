package co.wadcorp.waiting.data.domain.shop;

import java.util.List;
import java.util.Optional;

public interface ShopRepository {

  ShopEntity save(ShopEntity shop);

  <S extends ShopEntity> List<S> saveAll(Iterable<S> entities);

  Optional<ShopEntity> findByShopId(String shopId);

  List<ShopEntity> findAllByShopIdIn(List<String> shopIds);

  void deleteAllInBatch();

}
