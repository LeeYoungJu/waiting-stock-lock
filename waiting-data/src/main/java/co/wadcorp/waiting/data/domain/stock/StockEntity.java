package co.wadcorp.waiting.data.domain.stock;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.support.BaseEntity;
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
@Table(name = "cw_stock",
    indexes = {
        @Index(name = "cw_stock_menu_id_operation_date_index", columnList = "menu_id, operation_date")
    }
)
@Entity
public class StockEntity extends BaseEntity {

  public static final StockEntity EMPTY = new StockEntity("", null, false, 0, 0, false);

  private static final int STOCK_WARNING_THRESHOLD = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

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
  private StockEntity(String menuId, LocalDate operationDate, boolean isUsedDailyStock, int stock,
      int salesQuantity, boolean isOutOfStock) {
    this.menuId = menuId;
    this.operationDate = operationDate;
    this.isUsedDailyStock = isUsedDailyStock;
    this.stock = stock;
    this.salesQuantity = salesQuantity;
    this.isOutOfStock = isOutOfStock;
  }

  public static StockEntity of(MenuEntity menu, LocalDate operationDate) {
    return StockEntity.builder()
        .menuId(menu.getMenuId())
        .operationDate(operationDate)
        .isUsedDailyStock(menu.isUsedDailyStock())
        .stock(menu.isUsedDailyStock()
            ? menu.getDailyStock()
            : 0
        )
        .salesQuantity(0)
        .isOutOfStock(false)
        .build();
  }

  public void increaseSalesQuantity(int quantity) {
    if(salesQuantity + quantity > stock) {
      throw AppException.ofBadRequest(ErrorCode.OUT_OF_STOCK);
    }

    salesQuantity += quantity;
  }

  public void updateDailyStock(boolean usedDailyStock, Integer dailyStock) {
    this.isUsedDailyStock = usedDailyStock;
    if (this.isUsedDailyStock) {
      this.stock = dailyStock;
    }
  }

  public void addDailyStock(int additionalQuantity) {
    this.stock += additionalQuantity;
  }

  public void updateOutOfStock(boolean isOutOfStock) {
    this.isOutOfStock = isOutOfStock;
  }

  public Integer getRemainingQuantity() {
    if (!isUsedDailyStock) {
      return 0;
    }

    return this.stock - this.salesQuantity;
  }

  public boolean checkOutOfStock(int quantity) {
    if (this.isOutOfStock) {
      return true;
    }
    if (!isUsedDailyStock) {
      return false;
    }

    Integer remainingQuantity = getRemainingQuantity();
    return remainingQuantity < quantity;
  }

  public boolean isStockUnderThreshold() {
    Integer remainingQuantity = getRemainingQuantity();
    if (remainingQuantity == null) {
      return false;
    }

    return remainingQuantity <= STOCK_WARNING_THRESHOLD;
  }

  public boolean isNotOutOfStock() {
    return !isOutOfStock;
  }

}
