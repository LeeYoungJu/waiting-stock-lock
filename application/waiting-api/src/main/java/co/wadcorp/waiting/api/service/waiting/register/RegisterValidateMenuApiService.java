package co.wadcorp.waiting.api.service.waiting.register;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.api.service.waiting.register.dto.request.ValidateWaitingOrderManuStockServiceRequest;
import co.wadcorp.waiting.api.service.waiting.register.dto.request.ValidateWaitingOrderManuStockServiceRequest.Menu;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterValidateMenuResponse;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterValidateMenuResponse.InvalidMenu;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.validator.MenuStockValidator;
import co.wadcorp.waiting.data.domain.stock.validator.exception.StockException;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.service.menu.MenuService;
import co.wadcorp.waiting.data.service.stock.StockService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RegisterValidateMenuApiService {

  private final MenuService menuService;
  private final StockService stockService;

  public void validateStock(String shopId, LocalDate operationDate,
      ValidateWaitingOrderManuStockServiceRequest request) {
    List<Menu> menus = request.getMenus();
    List<String> menuIds = convert(menus, Menu::getId);

    Map<String, MenuEntity> menuEntityMap = menuService.getMenuIdMenuEntityMap(
        menuIds);

    Map<String, StockEntity> menuStockMap = stockService.getMenuIdMenuStockMap(
        operationDate, menuIds);

    List<MenuQuantity> menuQuantities = request.toMenuQuantity();

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
          RegisterValidateMenuResponse.builder()
              .reason(errorCode.getCode())
              .menus(getMenus(invalidMenus))
              .build()
      );
    }
  }

  private List<InvalidMenu> getMenus(List<InvalidStockMenu> invalidMenus) {
    return invalidMenus.stream()
        .map(item -> InvalidMenu.builder()
            .id(item.getMenuId())
            .name(item.getName())
            .quantity(item.getQuantity())
            .isUsedDailyStock(item.isUsedDailyStock())
            .remainingQuantity(item.getRemainingQuantity())
            .isOutOfStock(item.isOutOfStock())
            .build())
        .toList();
  }
}
