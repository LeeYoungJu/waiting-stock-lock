package co.wadcorp.waiting.data.infra.stock;

import co.wadcorp.waiting.data.domain.stock.StockHistoryEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaStockHistoryRepository extends StockHistoryRepository,
    JpaRepository<StockHistoryEntity, Long> {

}
