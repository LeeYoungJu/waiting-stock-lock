package co.wadcorp.waiting.data.domain.stock.validator.exception;

import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import lombok.Getter;

@Getter
public class StockException extends Exception {

  private final ErrorCode errorCode;
  private final List<InvalidStockMenu> invalidMenus;

  public StockException(ErrorCode errorCode, List<InvalidStockMenu> invalidMenus) {
    this.errorCode = errorCode;
    this.invalidMenus = invalidMenus;
  }
}
