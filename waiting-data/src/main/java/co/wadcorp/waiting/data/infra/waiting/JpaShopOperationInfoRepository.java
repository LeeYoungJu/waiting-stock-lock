package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaShopOperationInfoRepository extends ShopOperationInfoRepository, JpaRepository<ShopOperationInfoEntity, Long> {

  @Query("select s from ShopOperationInfoEntity s where s.shopId = :shopId and s.operationDate >= :operationDate")
  List<ShopOperationInfoEntity> findByShopIdAndOperationDateAfterOrEqual(String shopId, LocalDate operationDate);

}
