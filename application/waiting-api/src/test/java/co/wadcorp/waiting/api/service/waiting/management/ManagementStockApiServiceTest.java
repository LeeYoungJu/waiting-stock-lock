package co.wadcorp.waiting.api.service.waiting.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest.ManagementStockCategoryServiceDto;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest.ManagementStockMenuServiceDto;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementStockListResponse;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementStockListResponse.ManagementStockCategoryDto;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuRepository;
import co.wadcorp.waiting.data.domain.menu.CategoryRepository;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.support.Price;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ManagementStockApiServiceTest extends IntegrationTest {

  @Autowired
  private ManagementStockApiService managementStockApiService;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private CategoryMenuRepository categoryMenuRepository;

  @Autowired
  private StockRepository stockRepository;

  @Autowired
  private StockHistoryRepository stockHistoryRepository;

  @DisplayName("재고 관리 현황을 카테고리, 메뉴 순서에 맞게 조회한다.")
  @Test
  void getStocks() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 31);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 2);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 2, 1000, true, 10);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 1, 2000, true, 10);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 4, 3000, true, 10);
    MenuEntity menu4 = createMenu(shopId, "메뉴4", 3, 4000, true, 10);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu3.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu4.getMenuId());

    createStock(menu1, operationDate, 0, false);
    createStock(menu2, operationDate, 0, false);
    createStock(menu3, operationDate, 0, false);
    createStock(menu4, operationDate, 0, false);
    createStock(menu1, operationDate.plusDays(1), 0, false); // 제외 대상

    // when
    ManagementStockListResponse result = managementStockApiService.getStocks(shopId, operationDate);

    // then
    List<ManagementStockCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(2)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(category2.getCategoryId(), category2.getName(), 1),
            tuple(category1.getCategoryId(), category1.getName(), 2)
        );

    ManagementStockCategoryDto categoryDto1 = categories.get(0);
    assertThat(categoryDto1.getMenus()).hasSize(2)
        .extracting("id", "name", "ordering", "remainingQuantity", "isOutOfStock")
        .containsExactly(
            tuple(menu4.getMenuId(), menu4.getName(), 3, 10, false),
            tuple(menu3.getMenuId(), menu3.getName(), 4, 10, false)
        );

    ManagementStockCategoryDto categoryDto2 = categories.get(1);
    assertThat(categoryDto2.getMenus()).hasSize(2)
        .extracting("id", "name", "ordering", "remainingQuantity", "isOutOfStock")
        .containsExactly(
            tuple(menu2.getMenuId(), menu2.getName(), 1, 10, false),
            tuple(menu1.getMenuId(), menu1.getName(), 2, 10, false)
        );
  }

  @DisplayName("재고가 특정 한계값(3개) 이하인 메뉴와 해당 카테고리를 표시해준다.")
  @Test
  void getStocksWithStockUnderThreshold() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 31);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000, true, 10);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000, true, 10);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3, 3000, true, 10);
    MenuEntity menu4 = createMenu(shopId, "메뉴4", 4, 4000, true, 10);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu3.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu4.getMenuId());

    createStock(menu1, operationDate, 6, false);
    createStock(menu2, operationDate, 7, false); // 표시 대상
    createStock(menu3, operationDate, 6, false);
    createStock(menu4, operationDate, 6, false);

    // when
    ManagementStockListResponse result = managementStockApiService.getStocks(shopId, operationDate);

    // then
    List<ManagementStockCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(2)
        .extracting("id", "isStockUnderThreshold")
        .containsExactly(
            tuple(category1.getCategoryId(), true),
            tuple(category2.getCategoryId(), false)
        );

    ManagementStockCategoryDto categoryDto1 = categories.get(0);
    assertThat(categoryDto1.getMenus()).hasSize(2)
        .extracting("id", "isStockUnderThreshold")
        .containsExactly(
            tuple(menu1.getMenuId(), false),
            tuple(menu2.getMenuId(), true)
        );

    ManagementStockCategoryDto categoryDto2 = categories.get(1);
    assertThat(categoryDto2.getMenus()).hasSize(2)
        .extracting("id", "isStockUnderThreshold")
        .containsExactly(
            tuple(menu3.getMenuId(), false),
            tuple(menu4.getMenuId(), false)
        );
  }

  @DisplayName("재고가 품절 처리된 경우는 카테고리에 재고 부족 여부를 표시하지 않는다.")
  @Test
  void getStocksWithStockUnderThresholdWhenItsOutOfStock() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 31);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000, true, 10);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000, true, 10);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());

    createStock(menu1, operationDate, 6, false);
    createStock(menu2, operationDate, 7, true); // 원래는 표시 대상이지만 품절 메뉴라 포함하지 않는다.

    // when
    ManagementStockListResponse result = managementStockApiService.getStocks(shopId, operationDate);

    // then
    List<ManagementStockCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(1)
        .extracting("id", "isStockUnderThreshold")
        .containsExactly(
            tuple(category1.getCategoryId(), false)
        );

    ManagementStockCategoryDto categoryDto1 = categories.get(0);
    assertThat(categoryDto1.getMenus()).hasSize(2)
        .extracting("id", "isStockUnderThreshold")
        .containsExactly(
            tuple(menu1.getMenuId(), false),
            tuple(menu2.getMenuId(), true)
        );
  }

  @DisplayName("메뉴가 없는 카테고리는 제외하고 응답한다.")
  @Test
  void getStocksWithoutCategoryHavingNoMenus() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 31);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2); // 재고 관련 메뉴 없음
    CategoryEntity category3 = createCategory(shopId, "카테고리3", 3); // 메뉴 없음

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000, true, 10);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000, false, 0);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu2.getMenuId());

    createStock(menu1, operationDate, 0, false);

    // when
    ManagementStockListResponse result = managementStockApiService.getStocks(shopId, operationDate);

    // then
    List<ManagementStockCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(1)
        .extracting("id", "name")
        .containsExactly(
            tuple(category1.getCategoryId(), category1.getName())
        );
  }

  @DisplayName("재고를 사용하지 않는 메뉴는 제외하고 응답한다.")
  @Test
  void getStocksWith() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 31);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000, true, 10);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000, false, 10); // 제외 대상

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());

    createStock(menu1, operationDate, 0, false);
    createStock(menu2, operationDate, 0, false);

    // when
    ManagementStockListResponse result = managementStockApiService.getStocks(shopId, operationDate);

    // then
    List<ManagementStockCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(1)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(category1.getCategoryId(), category1.getName(), 1)
        );

    ManagementStockCategoryDto categoryDto = categories.get(0);
    assertThat(categoryDto.getMenus()).hasSize(1)
        .extracting("id", "name", "ordering", "remainingQuantity", "isOutOfStock")
        .containsExactly(
            tuple(menu1.getMenuId(), menu1.getName(), 1, 10, false)
        );
  }

  @DisplayName("재고 변경 시 당일 총재고에 대한 추가수량과 품절 여부를 반영할 수 있다.")
  @Test
  void updateStocks() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 31);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000, true, 10);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000, true, 10);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());

    createStock(menu1, operationDate, 6, false);
    createStock(menu2, operationDate, 6, false);
    createStock(menu1, operationDate.plusDays(1), 6, false); // 대상 아님

    UpdateStockServiceRequest request = UpdateStockServiceRequest.builder()
        .categories(List.of(ManagementStockCategoryServiceDto.builder()
            .id(category1.getCategoryId())
            .name(category1.getName())
            .menus(List.of(
                ManagementStockMenuServiceDto.builder()
                    .id(menu1.getMenuId())
                    .name(menu1.getName())
                    .additionalQuantity(1)
                    .isOutOfStock(false)
                    .build(),
                ManagementStockMenuServiceDto.builder()
                    .id(menu2.getMenuId())
                    .name(menu2.getName())
                    .additionalQuantity(-1)
                    .isOutOfStock(true)
                    .build()
            ))
            .build()
        ))
        .build();

    // when
    ManagementStockListResponse result = managementStockApiService.updateStocks(shopId, request,
        operationDate, "deviceId");

    // then
    List<ManagementStockCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(1)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(category1.getCategoryId(), category1.getName(), 1)
        );

    ManagementStockCategoryDto categoryDto1 = categories.get(0);
    assertThat(categoryDto1.getMenus()).hasSize(2)
        .extracting("id", "name", "ordering", "remainingQuantity", "isOutOfStock")
        .containsExactly(
            tuple(menu1.getMenuId(), menu1.getName(), 1, 5, false),
            tuple(menu2.getMenuId(), menu2.getName(), 2, 3, true)
        );

    assertThat(stockRepository.findAll()).hasSize(3)
        .extracting("menuId", "operationDate", "stock", "isOutOfStock")
        .contains(
            tuple(menu1.getMenuId(), operationDate, 11, false),
            tuple(menu2.getMenuId(), operationDate, 9, true),
            tuple(menu1.getMenuId(), operationDate.plusDays(1), 10, false)
        );

    assertThat(stockHistoryRepository.findAll()).hasSize(2)
        .extracting("menuId", "operationDate", "stock", "isOutOfStock")
        .contains(
            tuple(menu1.getMenuId(), operationDate, 11, false),
            tuple(menu2.getMenuId(), operationDate, 9, true)
        );
  }

  private CategoryEntity createCategory(String shopId, String name, int ordering) {
    CategoryEntity category = CategoryEntity.builder()
        .categoryId(UUIDUtil.shortUUID())
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .build();
    return categoryRepository.save(category);
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

  private CategoryMenuEntity createCategoryMenu(String categoryId, String menuId) {
    CategoryMenuEntity categoryMenu = CategoryMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(menuId)
        .build();
    return categoryMenuRepository.save(categoryMenu);
  }

  private StockEntity createStock(MenuEntity menu, LocalDate operationDate, int salesQuantity,
      boolean isOutOfStock) {
    StockEntity stock = StockEntity.builder()
        .menuId(menu.getMenuId())
        .operationDate(operationDate)
        .isUsedDailyStock(menu.isUsedDailyStock())
        .stock(menu.isUsedDailyStock()
            ? menu.getDailyStock()
            : 0
        )
        .salesQuantity(salesQuantity)
        .isOutOfStock(isOutOfStock)
        .build();
    return stockRepository.save(stock);
  }

}