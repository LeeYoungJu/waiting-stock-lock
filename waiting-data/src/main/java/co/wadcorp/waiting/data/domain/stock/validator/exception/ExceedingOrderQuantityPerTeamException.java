package co.wadcorp.waiting.data.domain.stock.validator.exception;

import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import lombok.Getter;

public class ExceedingOrderQuantityPerTeamException extends StockException {

  public ExceedingOrderQuantityPerTeamException(List<InvalidStockMenu> invalidMenus) {
    super(ErrorCode.EXCEEDING_ORDER_QUANTITY_PER_TEAM, invalidMenus);
  }

}
