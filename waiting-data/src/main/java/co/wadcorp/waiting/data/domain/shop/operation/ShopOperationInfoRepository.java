package co.wadcorp.waiting.data.domain.shop.operation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShopOperationInfoRepository {

  Optional<ShopOperationInfoEntity> findByShopIdAndOperationDate(String shopId, LocalDate operationDate);

  List<ShopOperationInfoEntity> findByShopIdAndOperationDateAfterOrEqual(String shopId, LocalDate operationDate);

  List<ShopOperationInfoEntity> findAllByShopIdInAndOperationDate(List<String> shopIds, LocalDate operationDate);

  ShopOperationInfoEntity save(ShopOperationInfoEntity shopOperationInfoEntity);

  <S extends ShopOperationInfoEntity> List<S> saveAll(Iterable<S> entities);

  List<ShopOperationInfoEntity> findAll();

  void deleteAllInBatch();

}
