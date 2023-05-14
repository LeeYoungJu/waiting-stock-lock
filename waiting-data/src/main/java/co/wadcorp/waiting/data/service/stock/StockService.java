package co.wadcorp.waiting.data.service.stock;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.service.stock.dto.StockMenuDto;
import co.wadcorp.waiting.shared.util.LocalDateTimeUtils;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class StockService {

  private final StockRepository stockRepository;
  private final StockHistoryRepository stockHistoryRepository;

  private final JdbcTemplate jdbcTemplate;

  public StockEntity save(StockEntity stock) {
    StockEntity savedStock = stockRepository.save(stock);
    saveHistory(savedStock);

    return savedStock;
  }

  public void updateDailyStock(MenuEntity menu, LocalDate operationDate) {
    List<StockEntity> stockEntities = stockRepository.findByMenuIdAndOperationDateAfterOrEqual(
        menu.getMenuId(), operationDate);

    stockEntities.forEach(stock -> {
      stock.updateDailyStock(menu.isUsedDailyStock(), menu.getDailyStock());

      saveHistory(stock);
    });
  }

  public void addDailyStockAndUpdateOutOfStock(List<StockMenuDto> stockMenuDtos,
      LocalDate operationDate) {
    Map<String, StockEntity> stockMap = createStockMap(stockMenuDtos, operationDate);

    stockMenuDtos.forEach(menuDto -> {
      StockEntity stock = stockMap.get(menuDto.getId());
      stock.addDailyStock(menuDto.getAdditionalQuantity());
      stock.updateOutOfStock(menuDto.isOutOfStock());

      saveHistory(stock);
    });
  }

  public List<StockEntity> getStocks(List<String> menuIds, LocalDate operationDate) {
    return stockRepository.findAllByMenuIdInAndOperationDate(menuIds, operationDate);
  }

  // TODO: 2023/03/30 Query & ApiService로 이동
  public Map<String, StockEntity> getMenuIdMenuStockMap(LocalDate operationDate,
      List<String> menuIds) {
    List<StockEntity> stocks = getStocks(menuIds, operationDate);
    return stocks.stream()
        .collect(Collectors.toMap(StockEntity::getMenuId, item -> item));
  }


  // 동일한 운영일에 대해서만
  public void saveByBatch(List<StockEntity> entities, LocalDate operationDate) {
    List<String> menuIds = entities.stream()
        .map(StockEntity::getMenuId)
        .toList();

    Set<String> existingMenuIds = stockRepository.findAllByMenuIdInAndOperationDate(menuIds,
            operationDate)
        .stream()
        .map(StockEntity::getMenuId)
        .collect(Collectors.toSet());

    List<StockEntity> filteredEntities = entities.stream()
        .filter(item -> !existingMenuIds.contains(item.getMenuId()))
        .toList();

    jdbcTemplate.batchUpdate(
        DailyStockInitBatchInsertSupport.getSqlOfDailyStock(),
        DailyStockInitBatchInsertSupport.getPreparedStatementSetterOfDailyStock(
            filteredEntities
        )
    );

    List<String> savedStockMenuIds = filteredEntities.stream()
        .map(StockEntity::getMenuId)
        .toList();

    List<StockEntity> savedEntities = stockRepository.findAllByMenuIdInAndOperationDate(
        savedStockMenuIds, operationDate
    );

    jdbcTemplate.batchUpdate(
        DailyStockInitBatchInsertSupport.getSqlOfOperationInfoHistory(),
        DailyStockInitBatchInsertSupport.getPreparedStatementSetterOfOperationInfoHistory(
            savedEntities
        )
    );

  }

  private Map<String, StockEntity> createStockMap(List<StockMenuDto> stockMenuDtos,
      LocalDate operationDate) {
    List<String> menuIds = convert(stockMenuDtos, StockMenuDto::getId);

    List<StockEntity> stocks = stockRepository.findAllByMenuIdInAndOperationDate(menuIds,
        operationDate);
    return stocks.stream()
        .collect(Collectors.toMap(StockEntity::getMenuId, entity -> entity));
  }

  private void saveHistory(StockEntity stock) {
    stockHistoryRepository.save(StockHistoryEntity.of(stock));
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  private static class DailyStockInitBatchInsertSupport {

    public static String getSqlOfDailyStock() {
      return """
          INSERT INTO cw_stock
           (menu_id, operation_date, used_daily_stock_yn, stock, sales_quantity,
            out_of_stock_yn, reg_date_time, mod_date_time)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?)
          """;
    }

    public static String getSqlOfOperationInfoHistory() {
      return """
          INSERT INTO cw_stock_history
           (menu_id, operation_date, used_daily_stock_yn, stock, sales_quantity,
            out_of_stock_yn, reg_date_time, stock_seq)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?)
          """;
    }

    public static BatchPreparedStatementSetter getPreparedStatementSetterOfDailyStock(
        List<StockEntity> entities
    ) {
      return new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          StockEntity entity = entities.get(i);
          ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();

          setPreparedStatement(ps, entity, nowDateTime);
          ps.setTimestamp(8, LocalDateTimeUtils.convertToTimestamp(nowDateTime));
        }

        @Override
        public int getBatchSize() {
          return entities.size();
        }
      };
    }

    public static BatchPreparedStatementSetter getPreparedStatementSetterOfOperationInfoHistory(
        List<StockEntity> entities
    ) {
      return new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          StockEntity entity = entities.get(i);
          ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();

          setPreparedStatement(ps, entity, nowDateTime);
          ps.setLong(8, entity.getSeq());
        }

        @Override
        public int getBatchSize() {
          return entities.size();
        }
      };
    }

    public static void setPreparedStatement(
        PreparedStatement ps, StockEntity entity, ZonedDateTime nowDateTime
    ) throws SQLException {

      ps.setString(1, entity.getMenuId());
      ps.setString(2, LocalDateUtils.convertToString(entity.getOperationDate()));
      ps.setString(3, BooleanUtils.isTrue(entity.isUsedDailyStock()) ? "Y" : "N");
      ps.setInt(4, entity.getStock());
      ps.setInt(5, entity.getSalesQuantity());
      ps.setString(6, BooleanUtils.isTrue(entity.isOutOfStock()) ? "Y" : "N");
      ps.setTimestamp(7, LocalDateTimeUtils.convertToTimestamp(nowDateTime));
    }
  }
}
