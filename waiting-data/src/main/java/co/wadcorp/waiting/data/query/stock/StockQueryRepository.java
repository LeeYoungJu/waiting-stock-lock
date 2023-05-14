package co.wadcorp.waiting.data.query.stock;

import static co.wadcorp.waiting.data.domain.stock.QStockEntity.stockEntity;

import co.wadcorp.waiting.data.domain.stock.StockEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class StockQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<StockEntity> findAllBy(List<String> menuId, LocalDate operationDate) {
    return queryFactory.select(stockEntity)
        .from(stockEntity)
        .where(
            stockEntity.menuId.in(menuId),
            stockEntity.operationDate.eq(operationDate)
        )
        .fetch();
  }

  public List<StockEntity> findAllUsedStocksBy(List<String> menuId, LocalDate operationDate) {
    return queryFactory.select(stockEntity)
        .from(stockEntity)
        .where(
            stockEntity.menuId.in(menuId),
            stockEntity.operationDate.eq(operationDate),
            stockEntity.isUsedDailyStock.isTrue()
        )
        .fetch();
  }

}
