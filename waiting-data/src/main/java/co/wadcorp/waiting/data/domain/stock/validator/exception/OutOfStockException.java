package co.wadcorp.waiting.data.domain.stock.validator.exception;

import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;

public class OutOfStockException extends StockException {

  public OutOfStockException(List<InvalidStockMenu> invalidMenus) {
    super(ErrorCode.OUT_OF_STOCK, invalidMenus);
  }
}
