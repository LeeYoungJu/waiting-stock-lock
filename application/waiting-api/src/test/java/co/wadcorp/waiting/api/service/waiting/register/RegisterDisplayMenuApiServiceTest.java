package co.wadcorp.waiting.api.service.waiting.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse.CategoryDto;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse.MenuDto;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryRepository;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuRepository;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryRepository;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RegisterDisplayMenuApiServiceTest extends IntegrationTest {

  @Autowired
  private RegisterDisplayMenuApiService registerDisplayMenuApiService;

  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private MenuRepository menuRepository;
  @Autowired
  private DisplayCategoryRepository displayCategoryRepository;
  @Autowired
  private DisplayMenuRepository displayMenuRepository;

  @Autowired
  private StockRepository stockRepository;

  @Test
  void getOrderMenu() {
    // given
    String shopId = "SHOP_ID";
    String categoryId = "CATEGORY_ID";
    String menuId = "MENU_ID";
    LocalDate operationDate = LocalDate.of(2023, 3, 23);

    CategoryEntity category = createCategory(shopId, categoryId);
    MenuEntity menuEntity = createMenu(shopId, menuId);
    DisplayCategoryEntity displayCategory = createDisplayCategory(shopId, categoryId);
    DisplayMenuEntity displayMenu = createMenu(shopId, categoryId, menuId);
    StockEntity stock = createStock(menuId, operationDate);

    // when

    RegisterWaitingOrderMenuResponse response = registerDisplayMenuApiService.getOrderMenu(
        shopId,
        DisplayMappingType.SHOP,
        operationDate
    );

    // then
    List<CategoryDto> categories = response.getCategories();
    assertThat(categories.size()).isEqualTo(1);

    CategoryDto categoryDto = categories.get(0);
    assertThat(categories).hasSize(1)
        .extracting(
            "id", "name", "ordering"
        )
        .containsExactly(
            Tuple.tuple(categoryId, "카테고리1", 1)
        );

    List<MenuDto> menus = categoryDto.getMenus();
    assertThat(menus.size()).isEqualTo(1);

    assertThat(menus).hasSize(1)
        .extracting(
            "id", "name", "ordering",
            "unitPrice", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam",
            "remainingQuantity"
        )
        .containsExactly(
            Tuple.tuple(menuId, "메뉴1", 1,
            new BigDecimal("10000.00"), true, 1,
            1000
        ));
  }

  @Test
  void validateStock() {
    // given

    // when

    // then
  }

  private CategoryEntity createCategory(String shopId, String categoryId) {
    return categoryRepository.save(CategoryEntity.builder()
        .shopId(shopId)
        .categoryId(categoryId)
        .name("카테고리1")
        .ordering(1)
        .build()
    );
  }

  private MenuEntity createMenu(String shopId, String menuId) {
    return menuRepository.save(MenuEntity.builder()
        .shopId(shopId)
        .menuId(menuId)
        .ordering(1)
        .name("메뉴1")
        .unitPrice(Price.of(10000))
        .isUsedDailyStock(true)
        .dailyStock(1000)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(1)
        .build()
    );
  }

  private DisplayCategoryEntity createDisplayCategory(String shopId, String categoryId) {
    return displayCategoryRepository.save(
        DisplayCategoryEntity.builder()
            .shopId(shopId)
            .categoryId(categoryId)
            .ordering(1)
            .displayMappingType(DisplayMappingType.SHOP)
            .build()
    );
  }

  private DisplayMenuEntity createMenu(String shopId, String categoryId, String menuId) {
    return displayMenuRepository.save(
        DisplayMenuEntity.builder()
            .shopId(shopId)
            .menuId(menuId)
            .categoryId(categoryId)
            .ordering(1)
            .menuName("메뉴1")
            .unitPrice(Price.of(10000))
            .isChecked(true)
            .displayMappingType(DisplayMappingType.SHOP)
            .build()

    );
  }

  private StockEntity createStock(String menuId, LocalDate operationDate) {
    return stockRepository.save(StockEntity.builder()
            .menuId(menuId)
            .operationDate(operationDate)
            .isUsedDailyStock(true)
            .stock(1000)
            .salesQuantity(0)
        .build());
  }
}

