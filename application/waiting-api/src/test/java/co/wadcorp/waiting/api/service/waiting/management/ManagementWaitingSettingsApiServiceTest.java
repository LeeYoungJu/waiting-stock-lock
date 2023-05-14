package co.wadcorp.waiting.api.service.waiting.management;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.model.settings.response.ManagementSettingsResponse;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingSettingsApiService;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.support.Price;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ManagementWaitingSettingsApiServiceTest extends IntegrationTest {

  @Autowired
  private ManagementWaitingSettingsApiService managementWaitingSettingsApiService;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private StockRepository stockRepository;

  @DisplayName("대시보드 조회 시 재고 임계값 이하인 메뉴의 수를 같이 응답한다.")
  @Test
  void getAllManagementSettingsWithCountOfMenusUnderStockThreshold() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 4, 6);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000, true, 10);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000, true, 10);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3, 3000, true, 10); // 해당 없음
    MenuEntity menu4 = createMenu(shopId, "메뉴4", 4, 4000, false, 10);

    createStock(menu1, operationDate, 7);
    createStock(menu2, operationDate, 7);
    createStock(menu3, operationDate, 6);
    createStock(menu4, operationDate, 7); // 해당 없음

    // when
    ManagementSettingsResponse result = managementWaitingSettingsApiService.getAllManagementSettings(
        shopId, operationDate);

    // then
    assertThat(result.getOrderSettings().getCountOfMenusUnderStockThreshold()).isEqualTo(2);
  }

  private MenuEntity createMenu(String shopId, String name, int ordering, long unitPrice,
      boolean isUsedDailyStock, int dailyStock) {
    MenuEntity menu = MenuEntity.builder()
        .menuId(UUIDUtil.shortUUID())
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .unitPrice(Price.of(unitPrice))
        .isUsedDailyStock(isUsedDailyStock)
        .dailyStock(dailyStock)
        .build();
    return menuRepository.save(menu);
  }

  private StockEntity createStock(MenuEntity menu, LocalDate operationDate, int salesQuantity) {
    StockEntity stock = StockEntity.builder()
        .menuId(menu.getMenuId())
        .operationDate(operationDate)
        .isUsedDailyStock(menu.isUsedDailyStock())
        .stock(menu.isUsedDailyStock()
            ? menu.getDailyStock()
            : 0
        )
        .salesQuantity(salesQuantity)
        .build();
    return stockRepository.save(stock);
  }

}