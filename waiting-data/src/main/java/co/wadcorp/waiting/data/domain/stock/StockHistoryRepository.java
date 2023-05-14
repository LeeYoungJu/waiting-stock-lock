package co.wadcorp.waiting.data.domain.stock;

import java.util.List;

public interface StockHistoryRepository {

  StockHistoryEntity save(StockHistoryEntity stockHistory);

  List<StockHistoryEntity> findAll();

  void deleteAllInBatch();
}
