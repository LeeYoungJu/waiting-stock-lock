package co.wadcorp.waiting.data.infra.waiting;

import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaShopOperationInfoHistoryRepository extends ShopOperationInfoHistoryRepository, JpaRepository<ShopOperationInfoHistoryEntity, Long> {
}
