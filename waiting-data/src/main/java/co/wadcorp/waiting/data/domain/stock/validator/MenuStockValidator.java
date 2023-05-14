package co.wadcorp.waiting.data.domain.stock.validator;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.InvalidStockMenu;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.validator.exception.ExceedingOrderQuantityPerTeamException;
import co.wadcorp.waiting.data.domain.stock.validator.exception.OutOfStockException;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Validator에서 발생하는 Execption은 핸들링이 꼭 필요하여 Checked Exception으로 만들었다.
 */
public class MenuStockValidator {

  public static void validateExceedingOrderQuantityPerTeam(
      List<MenuQuantity> menus,
      Map<String, MenuEntity> menuEntityMap,
      Map<String, StockEntity> menuStockMap
  ) throws ExceedingOrderQuantityPerTeamException {

    List<InvalidStockMenu> invalidMenus = new ArrayList<>();
    for (MenuQuantity menu : menus) {
      MenuEntity menuEntity = menuEntityMap.get(menu.getMenuId());
      if(Objects.isNull(menuEntity)) {
        throw AppException.ofBadRequest(ErrorCode.NOT_FOUND_MENU);
      }

      StockEntity menuRemainingStock = menuStockMap.get(menu.getMenuId());

      if (menuEntity.isInvalidMenuQuantityPerTeam(menu.getQuantity())) {
        invalidMenus.add(createInvalidMenu(menu, menuEntity, menuRemainingStock));
      }
    }

    if (!invalidMenus.isEmpty()) {
      throw new ExceedingOrderQuantityPerTeamException(invalidMenus);
    }
  }

  public static void validateOutOfStock(
      List<MenuQuantity> menus,
      Map<String, MenuEntity> menuEntityMap, Map<String, StockEntity> menuStockMap
  ) throws OutOfStockException {

    List<InvalidStockMenu> invalidMenus = new ArrayList<>();
    for (MenuQuantity menu : menus) {
      MenuEntity menuEntity = menuEntityMap.get(menu.getMenuId());
      StockEntity menuRemainingStock = menuStockMap.get(menu.getMenuId());

      if(Objects.isNull(menuRemainingStock)) {
        throw AppException.ofBadRequest(ErrorCode.NOT_FOUND_MENU_REMAINING_STOCK);
      }

      if (menuEntity.isNotUsedDailyStock()) {
        continue;
      }

      if (menuRemainingStock.checkOutOfStock(menu.getQuantity())) {
        invalidMenus.add(createInvalidMenu(menu, menuEntity, menuRemainingStock));
      }
    }

    if (!invalidMenus.isEmpty()) {
      throw new OutOfStockException(invalidMenus);
    }
  }

  private static InvalidStockMenu createInvalidMenu(MenuQuantity menu, MenuEntity menuEntity,
      StockEntity menuRemainingStock) {
    return InvalidStockMenu.builder()
        .menuId(menuEntity.getMenuId())
        .name(menuEntity.getName())
        .quantity(menu.getQuantity())
        .isUsedDailyStock(menuRemainingStock.isUsedDailyStock())
        .remainingQuantity(menuRemainingStock.getRemainingQuantity())
        .isOutOfStock(menuRemainingStock.isOutOfStock())
        .build();
  }

}
