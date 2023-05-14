package co.wadcorp.waiting.api.internal.service.waiting.validator;

import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteRegisterValidateMenuResponse.RemoteInvalidMenu;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.validator.MenuStockValidator;
import co.wadcorp.waiting.data.domain.stock.validator.exception.StockException;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

public class RemoteOrderMenuStockValidator {
  public static void validate(RemoteOrderDto orderDto,
      Map<String, MenuEntity> menuEntityMap, Map<String, StockEntity> menuStockMap) {
    List<MenuQuantity> menuQuantities = orderDto.toMenuQuantity();

    try {
      MenuStockValidator.validateExceedingOrderQuantityPerTeam(menuQuantities, menuEntityMap,
          menuStockMap
      );
      MenuStockValidator.validateOutOfStock(menuQuantities, menuEntityMap, menuStockMap);

    } catch (StockException e) {
      ErrorCode errorCode = e.getErrorCode();
      List<InvalidStockMenu> invalidMenus = e.getInvalidMenus();

      throw new AppException(
          HttpStatus.BAD_REQUEST,
          errorCode.getMessage(),
          errorCode.getMessage(),
          RemoteRegisterValidateMenuResponse.builder()
              .reason(errorCode.getCode())
              .menus(invalidMenus.stream()
                  .map(RemoteInvalidMenu::of)
                  .toList()
              )
              .build()
      );
    }
  }
}
