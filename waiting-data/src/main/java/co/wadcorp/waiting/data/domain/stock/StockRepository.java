package co.wadcorp.waiting.data.domain.stock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockRepository {

  StockEntity save(StockEntity stock);

  List<StockEntity> findAll();

  List<StockEntity> findAllByMenuIdInAndOperationDate(List<String> menuIds,
      LocalDate operationDate);

  void increaseSalesQuantity(String menuId, LocalDate operationDate, int quantity);

  void decreaseSalesQuantity(String menuId, LocalDate operationDate, int quantity);

  Optional<StockEntity> findByMenuIdAndOperationDate(String menuId, LocalDate operationDate);

  List<StockEntity> findByMenuIdAndOperationDateAfterOrEqual(String menuId,
      LocalDate operationDate);

  void deleteAllInBatch();
}
