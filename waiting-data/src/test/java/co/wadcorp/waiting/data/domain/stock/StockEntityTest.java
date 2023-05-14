package co.wadcorp.waiting.data.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StockEntityTest {

  @DisplayName("재고가 특정 한계값(3개) 이하인 경우를 체크할 수 있다.")
  @CsvSource({"10,6,false", "10,7,true"})
  @ParameterizedTest
  void isStockUnderThreshold(int stock, int salesQuantity, boolean expected) {
    // given
    StockEntity stockEntity = createStock(stock, salesQuantity);

    // when
    boolean result = stockEntity.isStockUnderThreshold();

    // then
    assertThat(result).isEqualTo(expected);
  }

  @DisplayName("재고 변경 시 추가수량을 추가하면 총재고가 변경된다.")
  @Test
  void addDailyStock() {
    // given
    int stock = 10;
    int salesQuantity = 6;

    StockEntity stockEntity = createStock(stock, salesQuantity);

    // when
    stockEntity.addDailyStock(1);

    // then
    assertThat(stockEntity.getStock()).isEqualTo(11);
    assertThat(stockEntity.getRemainingQuantity()).isEqualTo(5);
  }

  @DisplayName("재고 품절 처리를 할 수 있다.")
  @Test
  void updateOutOfStock() {
    // given
    StockEntity stockEntity = createStock(10, 6);

    // when
    stockEntity.updateOutOfStock(true);

    // then
    assertThat(stockEntity.isOutOfStock()).isTrue();
  }

  private StockEntity createStock(int stock, int salesQuantity) {
    return StockEntity.builder()
        .menuId("menuId")
        .operationDate(LocalDate.of(2023, 3, 31))
        .isUsedDailyStock(true)
        .stock(stock)
        .salesQuantity(salesQuantity)
        .isOutOfStock(false)
        .build();
  }

}