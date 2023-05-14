package co.wadcorp.waiting.data.domain.stock;

import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cw_stock_history",
    indexes = {
        @Index(name = "cw_stock_history_stock_seq_index", columnList = "stock_seq"),
        @Index(name = "cw_stock_history_menu_id_operation_date_index", columnList = "menu_id, operation_date")
    }
)
@Entity
public class StockHistoryEntity extends BaseHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "stock_seq")
  private Long stockSeq;

  @Column(name = "menu_id")
  private String menuId;

  @Column(name = "operation_date")
  private LocalDate operationDate;

  @Column(name = "used_daily_stock_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isUsedDailyStock;

  @Column(name = "stock")
  private int stock;

  @Column(name = "sales_quantity")
  private int salesQuantity;

  @Column(name = "out_of_stock_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isOutOfStock;

  @Builder
  private StockHistoryEntity(Long stockSeq, String menuId, LocalDate operationDate,
      boolean isUsedDailyStock, int stock, int salesQuantity, boolean isOutOfStock) {
    this.stockSeq = stockSeq;
    this.menuId = menuId;
    this.operationDate = operationDate;
    this.isUsedDailyStock = isUsedDailyStock;
    this.stock = stock;
    this.salesQuantity = salesQuantity;
    this.isOutOfStock = isOutOfStock;
  }

  public static StockHistoryEntity of(StockEntity stockEntity) {
    return StockHistoryEntity.builder()
        .stockSeq(stockEntity.getSeq())
        .menuId(stockEntity.getMenuId())
        .operationDate(stockEntity.getOperationDate())
        .isUsedDailyStock(stockEntity.isUsedDailyStock())
        .stock(stockEntity.getStock())
        .salesQuantity(stockEntity.getSalesQuantity())
        .isOutOfStock(stockEntity.isOutOfStock())
        .build();
  }

}
