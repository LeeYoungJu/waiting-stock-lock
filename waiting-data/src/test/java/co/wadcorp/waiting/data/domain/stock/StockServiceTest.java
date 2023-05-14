package co.wadcorp.waiting.data.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.service.stock.StockService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StockServiceTest extends IntegrationTest {

  @Autowired
  private StockRepository stockRepository;
  @Autowired
  private StockHistoryRepository stockHistoryRepository;
  @Autowired
  private StockService stockService;

  @AfterEach
  void tearDown() {
    stockRepository.deleteAllInBatch();
    stockHistoryRepository.deleteAllInBatch();
  }

  @DisplayName("특정 운영일자 이후의 모든 재고에 대한 재고 설정을 변경한다.")
  @Test
  void updateDailyStock() {
    // given
    String menuId = "menuId";
    LocalDate operationDate = LocalDate.of(2023, 4, 6);

    createStock(menuId, false, 10, operationDate.minusDays(2), 0, false);
    createStock(menuId, false, 10, operationDate.minusDays(1), 0, false);
    createStock(menuId, false, 10, operationDate, 0, false);
    createStock(menuId, false, 10, operationDate.plusDays(1), 0, false);
    createStock(menuId, false, 10, operationDate.plusDays(2), 0, false);

    MenuEntity menu = MenuEntity.builder()
        .menuId(menuId)
        .isUsedDailyStock(true)
        .dailyStock(20)
        .build();

    // when
    stockService.updateDailyStock(menu, operationDate);

    // then
    assertThat(stockRepository.findAll()).hasSize(5)
        .extracting("operationDate", "isUsedDailyStock", "stock")
        .contains(
            tuple(operationDate.minusDays(2), false, 10),
            tuple(operationDate.minusDays(1), false, 10),
            tuple(operationDate, true, 20),
            tuple(operationDate.plusDays(1), true, 20),
            tuple(operationDate.plusDays(2), true, 20)
        );
  }

  private StockEntity createStock(String menuId, boolean isUsedDailyStock, int stock,
      LocalDate operationDate, int salesQuantity, boolean isOutOfStock) {
    StockEntity stockEntity = StockEntity.builder()
        .menuId(menuId)
        .operationDate(operationDate)
        .isUsedDailyStock(isUsedDailyStock)
        .stock(stock)
        .salesQuantity(salesQuantity)
        .isOutOfStock(isOutOfStock)
        .build();
    return stockRepository.save(stockEntity);
  }


}