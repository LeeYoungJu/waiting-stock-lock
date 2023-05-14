package co.wadcorp.waiting.data.infra.stock;

import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaStockRepository extends StockRepository, JpaRepository<StockEntity, Long> {

  List<StockEntity> findAllByMenuIdInAndOperationDate(List<String> menuId, LocalDate operationDate);

  @Transactional
  @Modifying
  @Query("""
      UPDATE StockEntity stock
      SET stock.salesQuantity = stock.salesQuantity + :quantity
      WHERE stock.menuId = :menuId
        AND stock.operationDate = :operationDate
      """)
  void increaseSalesQuantity(String menuId, LocalDate operationDate, int quantity);

  @Modifying
  @Query("""
      UPDATE StockEntity stock
      SET stock.salesQuantity = stock.salesQuantity - :quantity
      WHERE stock.menuId = :menuId
        AND stock.operationDate = :operationDate
      """)
  void decreaseSalesQuantity(String menuId, LocalDate operationDate, int quantity);

  @Query("select s from StockEntity s where s.menuId = :menuId and s.operationDate >= :operationDate")
  List<StockEntity> findByMenuIdAndOperationDateAfterOrEqual(String menuId, LocalDate operationDate);

}
