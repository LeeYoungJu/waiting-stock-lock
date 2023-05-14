package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.SHOP;
import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.TAKE_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderMenuSettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsListResponse.OrderCategoryDto;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsResponse;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryRepository;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuRepository;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuRepository;
import co.wadcorp.waiting.data.domain.menu.CategoryRepository;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryEntity;
import co.wadcorp.waiting.data.domain.stock.StockHistoryRepository;
import co.wadcorp.waiting.data.domain.stock.StockRepository;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OrderMenuSettingsApiServiceTest extends IntegrationTest {

  @Autowired
  private OrderMenuSettingsApiService orderMenuSettingsApiService;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private CategoryMenuRepository categoryMenuRepository;

  @Autowired
  private DisplayCategoryRepository displayCategoryRepository;

  @Autowired
  private DisplayMenuRepository displayMenuRepository;

  @Autowired
  private StockRepository stockRepository;

  @Autowired
  private StockHistoryRepository stockHistoryRepository;

  @DisplayName("카테고리-메뉴 정보를 순서에 맞게 조회한다.")
  @Test
  void getMenus() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 2);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 2, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 1, 2000);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 4, 3000);
    MenuEntity menu4 = createMenu(shopId, "메뉴4", 3, 4000);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu3.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu4.getMenuId());

    // when
    OrderMenuSettingsListResponse result = orderMenuSettingsApiService.getMenus(shopId);

    // then
    List<OrderCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(2)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(category2.getCategoryId(), category2.getName(), category2.getOrdering()),
            tuple(category1.getCategoryId(), category1.getName(), category1.getOrdering())
        );

    assertThat(categories.get(0).getMenus()).hasSize(2)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(menu4.getMenuId(), menu4.getName(), menu4.getOrdering()),
            tuple(menu3.getMenuId(), menu3.getName(), menu3.getOrdering())
        );
    assertThat(categories.get(1).getMenus()).hasSize(2)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(menu2.getMenuId(), menu2.getName(), menu2.getOrdering()),
            tuple(menu1.getMenuId(), menu1.getName(), menu1.getOrdering())
        );
  }

  @DisplayName("카테고리-메뉴 정보 조회 시 메뉴가 없어도 카테고리를 조회할 수 있다.")
  @Test
  void getCategoriesWithEmptyMenus() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 2);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 1);

    // when
    OrderMenuSettingsListResponse result = orderMenuSettingsApiService.getMenus(shopId);

    // then
    List<OrderCategoryDto> categories = result.categories();
    assertThat(categories).hasSize(2)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(category2.getCategoryId(), category2.getName(), category2.getOrdering()),
            tuple(category1.getCategoryId(), category1.getName(), category1.getOrdering())
        );

    assertThat(categories.get(0).getMenus()).isEmpty();
    assertThat(categories.get(1).getMenus()).isEmpty();
  }


  @DisplayName("메뉴 단건 정보를 조회한다.")
  @Test
  void getMenu() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());

    // when
    OrderMenuSettingsResponse result = orderMenuSettingsApiService.getMenu(menu1.getMenuId());

    // then
    assertThat(result)
        .extracting("id", "categoryId", "name", "unitPrice", "isUsedDailyStock",
            "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .containsExactly(menu1.getMenuId(), category1.getCategoryId(), menu1.getName(),
            menu1.getUnitPrice().value(), menu1.isUsedDailyStock(),
            menu1.getDailyStock(), menu1.isUsedMenuQuantityPerTeam(),
            menu1.getMenuQuantityPerTeam());
  }

  @DisplayName("신규 단건 메뉴를 생성한다.")
  @Test
  void create() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 30);
    String newMenuId = UUIDUtil.shortUUID();

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    createDisplayCategory(category1, SHOP, 1, true);
    createDisplayCategory(category1, TAKE_OUT, 1, true);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 2, 2000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 1, 1000);
    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 2);
    createDisplayMenu(category1.getCategoryId(), menu1, TAKE_OUT, 2);
    createDisplayMenu(category1.getCategoryId(), menu2, SHOP, 1);
    createDisplayMenu(category1.getCategoryId(), menu2, TAKE_OUT, 1);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());

    OrderMenuSettingsServiceRequest request = OrderMenuSettingsServiceRequest.builder()
        .id(newMenuId)
        .categoryId(category1.getCategoryId())
        .name("메뉴3")
        .unitPrice(BigDecimal.valueOf(8000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(4)
        .build();

    // when
    OrderMenuSettingsResponse result = orderMenuSettingsApiService.create(shopId, request,
        operationDate);

    // then
    assertThat(result)
        .extracting("id", "categoryId", "name", "unitPrice", "isUsedDailyStock",
            "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .containsExactly(newMenuId, category1.getCategoryId(), "메뉴3", BigDecimal.valueOf(8000),
            true, 300, true, 4);

    List<MenuEntity> menus = menuRepository.findAll();
    assertThat(menus).hasSize(3)
        .extracting("menuId", "ordering")
        .contains(
            tuple(menu1.getMenuId(), 2),
            tuple(menu2.getMenuId(), 1),
            tuple(newMenuId, 3)
        );

    List<CategoryMenuEntity> categoryMenus = categoryMenuRepository.findAll();
    assertThat(categoryMenus).hasSize(3)
        .extracting("categoryId", "menuId")
        .contains(
            tuple(category1.getCategoryId(), menu1.getMenuId()),
            tuple(category1.getCategoryId(), menu2.getMenuId()),
            tuple(category1.getCategoryId(), newMenuId)
        );

    List<DisplayMenuEntity> displayMenus = displayMenuRepository.findAll();
    assertThat(displayMenus).hasSize(6)
        .extracting("categoryId", "menuId", "shopId", "displayMappingType", "ordering", "menuName",
            "isChecked")
        .contains(
            tuple(category1.getCategoryId(), menu1.getMenuId(), shopId, SHOP, 2, "메뉴1", false),
            tuple(category1.getCategoryId(), menu1.getMenuId(), shopId, TAKE_OUT, 2, "메뉴1", false),
            tuple(category1.getCategoryId(), menu2.getMenuId(), shopId, SHOP, 1, "메뉴2", false),
            tuple(category1.getCategoryId(), menu2.getMenuId(), shopId, TAKE_OUT, 1, "메뉴2", false),
            tuple(category1.getCategoryId(), newMenuId, shopId, SHOP, 3, "메뉴3", true),
            tuple(category1.getCategoryId(), newMenuId, shopId, TAKE_OUT, 3, "메뉴3", true)
        );

    List<StockEntity> stocks = stockRepository.findAll();
    assertThat(stocks).hasSize(1)
        .extracting("menuId", "operationDate", "isUsedDailyStock", "stock", "salesQuantity")
        .contains(
            tuple(newMenuId, operationDate, true, 300, 0)
        );

    List<StockHistoryEntity> stockHistories = stockHistoryRepository.findAll();
    assertThat(stockHistories).hasSize(1)
        .extracting("menuId", "operationDate", "isUsedDailyStock", "stock", "salesQuantity")
        .contains(
            tuple(newMenuId, operationDate, true, 300, 0)
        );
  }

  @DisplayName("신규 단건 메뉴 생성 시 일일재고 사용여부, 팀당 주문가능여부 체크가 false라면 재고수, 팀당 주문가능수는 저장하지 않는다.")
  @Test
  void createWithoutUsingCheckParams() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 30);
    String newMenuId = UUIDUtil.shortUUID();

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    createDisplayCategory(category1, SHOP, 1, true);
    createDisplayCategory(category1, TAKE_OUT, 1, true);

    OrderMenuSettingsServiceRequest request = OrderMenuSettingsServiceRequest.builder()
        .id(newMenuId)
        .categoryId(category1.getCategoryId())
        .name("메뉴1")
        .unitPrice(BigDecimal.valueOf(8000))
        .isUsedDailyStock(false)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(false)
        .menuQuantityPerTeam(4)
        .build();

    // when
    OrderMenuSettingsResponse result = orderMenuSettingsApiService.create(shopId, request,
        operationDate);

    // then
    assertThat(result)
        .extracting("id", "categoryId", "name", "unitPrice", "isUsedDailyStock",
            "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .containsExactly(newMenuId, category1.getCategoryId(), "메뉴1", BigDecimal.valueOf(8000),
            false, 0, false, 0);

    List<MenuEntity> menus = menuRepository.findAll();
    assertThat(menus).hasSize(1)
        .extracting("menuId", "isUsedDailyStock", "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .contains(
            tuple(newMenuId, false, 0, false, 0)
        );

    List<StockEntity> stocks = stockRepository.findAll();
    assertThat(stocks).hasSize(1)
        .extracting("menuId", "operationDate", "isUsedDailyStock", "stock", "salesQuantity")
        .contains(
            tuple(newMenuId, operationDate, false, 0, 0)
        );
  }

  @DisplayName("단건 메뉴를 수정한다.")
  @Test
  void update() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 30);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 1, 2000);
    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 1);
    createDisplayMenu(category1.getCategoryId(), menu1, TAKE_OUT, 1);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu2.getMenuId());

    createStock(menu1, operationDate);

    OrderMenuSettingsServiceRequest request = OrderMenuSettingsServiceRequest.builder()
        .id(menu1.getMenuId())
        .categoryId(category2.getCategoryId()) // 카테고리 변경
        .name("냉면")
        .unitPrice(BigDecimal.valueOf(8000))
        .isUsedDailyStock(true)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(4)
        .build();

    // when
    OrderMenuSettingsResponse result = orderMenuSettingsApiService.update(menu1.getMenuId(),
        request, operationDate);

    // then
    assertThat(result)
        .extracting("id", "categoryId", "name", "unitPrice", "isUsedDailyStock",
            "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .containsExactly(menu1.getMenuId(), category2.getCategoryId(), "냉면",
            BigDecimal.valueOf(8000), true, 300, true, 4);

    List<MenuEntity> menus = menuRepository.findAll();
    assertThat(menus).hasSize(2)
        .extracting("menuId", "name", "ordering", "unitPrice", "isUsedDailyStock",
            "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .containsExactly(
            tuple(menu1.getMenuId(), "냉면", 2, Price.of(BigDecimal.valueOf(8000)), true, 300, true, 4),
            tuple(menu2.getMenuId(), "메뉴2", 1, Price.of(BigDecimal.valueOf(2000)), false, 0, false, 0)
        );

    List<CategoryMenuEntity> categoryMenus = categoryMenuRepository.findAll();
    assertThat(categoryMenus).hasSize(2)
        .extracting("categoryId", "menuId")
        .contains(
            tuple(category2.getCategoryId(), menu1.getMenuId()),
            tuple(category2.getCategoryId(), menu2.getMenuId())
        );

    List<DisplayMenuEntity> displayMenus = displayMenuRepository.findAll();
    assertThat(displayMenus).hasSize(2)
        .extracting("categoryId", "menuName", "unitPrice")
        .contains(
            tuple(category2.getCategoryId(), "냉면", Price.of(BigDecimal.valueOf(8000))),
            tuple(category2.getCategoryId(), "냉면", Price.of(BigDecimal.valueOf(8000)))
        );

    List<StockEntity> stocks = stockRepository.findAll();
    assertThat(stocks).hasSize(1)
        .extracting("menuId", "operationDate", "isUsedDailyStock", "stock", "salesQuantity")
        .contains(
            tuple(menu1.getMenuId(), operationDate, true, 300, 0)
        );

    List<StockHistoryEntity> stockHistories = stockHistoryRepository.findAll();
    assertThat(stockHistories).hasSize(1)
        .extracting("menuId", "operationDate", "isUsedDailyStock", "stock", "salesQuantity")
        .contains(
            tuple(menu1.getMenuId(), operationDate, true, 300, 0)
        );
  }

  @DisplayName("단건 메뉴 수정 시 일일재고 사용여부, 팀당 주문가능여부 체크가 false라면 재고수, 팀당 주문가능수는 업데이트하지 않는다.")
  @Test
  void updateWithoutUsingCheckParams() {
    // given
    String shopId = "shopId";
    LocalDate operationDate = LocalDate.of(2023, 3, 30);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 1);
    createDisplayMenu(category1.getCategoryId(), menu1, TAKE_OUT, 1);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());

    createStock(menu1, operationDate);

    OrderMenuSettingsServiceRequest request = OrderMenuSettingsServiceRequest.builder()
        .id(menu1.getMenuId())
        .categoryId(category1.getCategoryId())
        .name("냉면")
        .unitPrice(BigDecimal.valueOf(8000))
        .isUsedDailyStock(false)
        .dailyStock(300)
        .isUsedMenuQuantityPerTeam(false)
        .menuQuantityPerTeam(4)
        .build();

    // when
    OrderMenuSettingsResponse result = orderMenuSettingsApiService.update(menu1.getMenuId(),
        request, operationDate);

    // then
    assertThat(result)
        .extracting("id", "categoryId", "name", "unitPrice", "isUsedDailyStock",
            "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .containsExactly(menu1.getMenuId(), category1.getCategoryId(), "냉면",
            BigDecimal.valueOf(8000), false, 0, false, 0);

    List<MenuEntity> menus = menuRepository.findAll();
    assertThat(menus).hasSize(1)
        .extracting("menuId", "name", "unitPrice", "isUsedDailyStock",
            "dailyStock", "isUsedMenuQuantityPerTeam", "menuQuantityPerTeam")
        .containsExactly(
            tuple(menu1.getMenuId(), "냉면", Price.of(BigDecimal.valueOf(8000)), false, 0, false, 0)
        );

    List<StockEntity> stocks = stockRepository.findAll();
    assertThat(stocks).hasSize(1)
        .extracting("menuId", "operationDate", "isUsedDailyStock", "stock", "salesQuantity")
        .contains(
            tuple(menu1.getMenuId(), operationDate, false, 0, 0)
        );
  }

  @DisplayName("단건 메뉴를 삭제한다.")
  @Test
  void delete() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 1);
    createDisplayMenu(category1.getCategoryId(), menu1, TAKE_OUT, 1);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());

    // when
    orderMenuSettingsApiService.delete(menu1.getMenuId());

    // then
    List<MenuEntity> menus = menuRepository.findAll();
    assertThat(menus).hasSize(1)
        .extracting("menuId", "isDeleted")
        .containsExactly(
            tuple(menu1.getMenuId(), true)
        );

    List<CategoryMenuEntity> categoryMenus = categoryMenuRepository.findAll();
    assertThat(categoryMenus).isEmpty();

    List<DisplayMenuEntity> displayMenus = displayMenuRepository.findAll();
    assertThat(displayMenus).isEmpty();
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

  private MenuEntity createMenu(String shopId, String name, int ordering, long unitPrice) {
    MenuEntity menu = MenuEntity.builder()
        .menuId(UUIDUtil.shortUUID())
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .unitPrice(Price.of(unitPrice))
        .dailyStock(0)
        .menuQuantityPerTeam(0)
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

  private DisplayCategoryEntity createDisplayCategory(CategoryEntity category,
      DisplayMappingType displayMappingType, int ordering, boolean isAllChecked) {
    DisplayCategoryEntity displayCategory = DisplayCategoryEntity.builder()
        .categoryId(category.getCategoryId())
        .shopId(category.getShopId())
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .isAllChecked(isAllChecked)
        .build();
    return displayCategoryRepository.save(displayCategory);
  }

  private DisplayMenuEntity createDisplayMenu(String categoryId, MenuEntity menu,
      DisplayMappingType displayMappingType, int ordering) {
    DisplayMenuEntity displayMenu = DisplayMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(menu.getMenuId())
        .shopId(menu.getShopId())
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .menuName(menu.getName())
        .unitPrice(menu.getUnitPrice())
        .isChecked(false)
        .build();
    return displayMenuRepository.save(displayMenu);
  }

  private StockEntity createStock(MenuEntity menu, LocalDate operationDate) {
    StockEntity stock = StockEntity.builder()
        .menuId(menu.getMenuId())
        .operationDate(operationDate)
        .isUsedDailyStock(menu.isUsedDailyStock())
        .stock(menu.isUsedDailyStock()
            ? menu.getDailyStock()
            : 0
        )
        .salesQuantity(0)
        .build();
    return stockRepository.save(stock);
  }

}