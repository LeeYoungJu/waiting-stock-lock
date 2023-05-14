package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.SHOP;
import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.TAKE_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.settings.dto.request.MenuType;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryMappingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryMappingSaveServiceRequest.MappingMenuServiceDto;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryOrderingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryOrderingSaveServiceRequest.MappingCategoryServiceDto;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderDisplayMenuServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategoryOrderingServiceResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderDisplayMenuMappingResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderDisplayMenuMappingResponse.OrderDisplayCategoryDto;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuMappingServiceResponse;
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
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OrderMenuMappingApiServiceTest extends IntegrationTest {

  @Autowired
  private OrderMenuMappingApiService orderMenuMappingApiService;

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

  @DisplayName("메뉴판 매핑 카테고리, 메뉴 데이터를 순서에 맞게 조회한다.")
  @Test
  void getDisplayMappingMenus() {
    // given
    String shopId = "shopId";
    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);
    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3, 3000);
    MenuEntity menu4 = createMenu(shopId, "메뉴4", 4, 4000);
    MenuEntity menu5 = createMenu(shopId, "메뉴5", 5, 5000);
    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu3.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu4.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu5.getMenuId());

    createDisplayCategory(category1, SHOP, 2, true);
    createDisplayCategory(category1, TAKE_OUT, 1, false);
    createDisplayCategory(category2, SHOP, 1, false);
    createDisplayCategory(category2, TAKE_OUT, 2, false);

    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 2, true);
    createDisplayMenu(category1.getCategoryId(), menu2, SHOP, 3, true);
    createDisplayMenu(category1.getCategoryId(), menu3, SHOP, 1, false);
    createDisplayMenu(category1.getCategoryId(), menu1, TAKE_OUT, 1, true);
    createDisplayMenu(category2.getCategoryId(), menu4, SHOP, 2, true);
    createDisplayMenu(category2.getCategoryId(), menu5, SHOP, 1, true);

    OrderDisplayMenuServiceRequest request = OrderDisplayMenuServiceRequest.builder()
        .menuType(MenuType.SHOP_MENU)
        .build();

    // when
    OrderDisplayMenuMappingResponse result = orderMenuMappingApiService.getDisplayMappingMenus(
        shopId, request);

    // then
    List<OrderDisplayCategoryDto> categories = result.getCategories();
    assertThat(categories).hasSize(2)
        .extracting("id", "name", "ordering", "allChecked")
        .containsExactly(
            tuple(category2.getCategoryId(), category2.getName(), 1, false),
            tuple(category1.getCategoryId(), category1.getName(), 2, true)
        );

    OrderDisplayCategoryDto displayCategoryDto1 = categories.get(0);
    assertThat(displayCategoryDto1.getMenus()).hasSize(2)
        .extracting("id", "name", "ordering", "isChecked")
        .containsExactly(
            tuple(menu5.getMenuId(), menu5.getName(), 1, true),
            tuple(menu4.getMenuId(), menu4.getName(), 2, true)
        );

    OrderDisplayCategoryDto displayCategoryDto2 = categories.get(1);
    assertThat(displayCategoryDto2.getMenus()).hasSize(3)
        .extracting("id", "name", "ordering", "isChecked")
        .containsExactly(
            tuple(menu3.getMenuId(), menu3.getName(), 1, false),
            tuple(menu1.getMenuId(), menu1.getName(), 2, true),
            tuple(menu2.getMenuId(), menu2.getName(), 3, true)
        );
  }

  @DisplayName("메뉴판 저장 시 체크된 메뉴 항목과 순서에 맞게 반영한다.")
  @Test
  void saveMenuMapping() {
    // given
    String shopId = "shopId";
    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3, 3000);

    createDisplayCategory(category1.getCategoryId(), shopId, SHOP, 1, true);

    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 1, false);
    createDisplayMenu(category1.getCategoryId(), menu2, SHOP, 2, false);
    createDisplayMenu(category1.getCategoryId(), menu3, SHOP, 3, false);

    OrderCategoryMappingSaveServiceRequest request = OrderCategoryMappingSaveServiceRequest.builder()
        .displayMappingType(SHOP)
        .allChecked(true)
        .menus(List.of(
            MappingMenuServiceDto.builder()
                .id(menu1.getMenuId())
                .isChecked(true)
                .ordering(2)
                .build(),
            MappingMenuServiceDto.builder()
                .id(menu2.getMenuId())
                .isChecked(true)
                .ordering(3)
                .build(),
            MappingMenuServiceDto.builder()
                .id(menu3.getMenuId())
                .isChecked(true)
                .ordering(1)
                .build()
        ))
        .build();

    // when
    OrderMenuMappingServiceResponse result = orderMenuMappingApiService.saveMenuMapping(
        category1.getCategoryId(), request);

    // then
    assertThat(result)
        .extracting("id", "allChecked")
        .containsExactly(category1.getCategoryId(), true);

    assertThat(result.getMenus()).hasSize(3)
        .extracting("id", "name", "isChecked", "ordering")
        .containsExactly(
            tuple(menu3.getMenuId(), menu3.getName(), true, 1),
            tuple(menu1.getMenuId(), menu1.getName(), true, 2),
            tuple(menu2.getMenuId(), menu2.getName(), true, 3)
        );

    assertThat(displayCategoryRepository.findAll()).hasSize(1)
        .extracting("categoryId", "isAllChecked")
        .contains(
            tuple(category1.getCategoryId(), true)
        );

    assertThat(displayMenuRepository.findAll()).hasSize(3)
        .extracting("menuId", "isChecked", "ordering")
        .contains(
            tuple(menu1.getMenuId(), true, 2),
            tuple(menu2.getMenuId(), true, 3),
            tuple(menu3.getMenuId(), true, 1)
        );
  }

  @DisplayName("메뉴판 저장 시 전체 체크를 해제하면 반영된다.")
  @Test
  void saveMenuMappingReleasingAllChecked() {
    // given
    String shopId = "shopId";
    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3, 3000);

    createDisplayCategory(category1.getCategoryId(), shopId, SHOP, 1, true);

    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 1, true);
    createDisplayMenu(category1.getCategoryId(), menu2, SHOP, 2, true);
    createDisplayMenu(category1.getCategoryId(), menu3, SHOP, 3, true);

    OrderCategoryMappingSaveServiceRequest request = OrderCategoryMappingSaveServiceRequest.builder()
        .displayMappingType(SHOP)
        .allChecked(false)
        .menus(List.of(
            MappingMenuServiceDto.builder()
                .id(menu1.getMenuId())
                .isChecked(false)
                .ordering(2)
                .build(),
            MappingMenuServiceDto.builder()
                .id(menu2.getMenuId())
                .isChecked(false)
                .ordering(3)
                .build(),
            MappingMenuServiceDto.builder()
                .id(menu3.getMenuId())
                .isChecked(false)
                .ordering(1)
                .build()
        ))
        .build();

    // when
    OrderMenuMappingServiceResponse result = orderMenuMappingApiService.saveMenuMapping(
        category1.getCategoryId(), request);

    // then
    assertThat(result)
        .extracting("id", "allChecked")
        .containsExactly(category1.getCategoryId(), false);

    assertThat(result.getMenus()).hasSize(3)
        .extracting("id", "name", "isChecked", "ordering")
        .containsExactly(
            tuple(menu3.getMenuId(), menu3.getName(), false, 1),
            tuple(menu1.getMenuId(), menu1.getName(), false, 2),
            tuple(menu2.getMenuId(), menu2.getName(), false, 3)
        );

    assertThat(displayCategoryRepository.findAll()).hasSize(1)
        .extracting("categoryId", "isAllChecked")
        .contains(
            tuple(category1.getCategoryId(), false)
        );

    assertThat(displayMenuRepository.findAll()).hasSize(3)
        .extracting("menuId", "isChecked", "ordering")
        .contains(
            tuple(menu1.getMenuId(), false, 2),
            tuple(menu2.getMenuId(), false, 3),
            tuple(menu3.getMenuId(), false, 1)
        );
  }

  @DisplayName("메뉴판 카테고리 순서를 저장한다.")
  @Test
  void saveCategoryOrdering() {
    // given
    String shopId = "shopId";
    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);
    CategoryEntity category3 = createCategory(shopId, "카테고리3", 3);

    createDisplayCategory(category1.getCategoryId(), shopId, SHOP, 1, true);
    createDisplayCategory(category2.getCategoryId(), shopId, SHOP, 2, true);
    createDisplayCategory(category3.getCategoryId(), shopId, SHOP, 3, true);
    createDisplayCategory(category1.getCategoryId(), shopId, TAKE_OUT, 1, true);
    createDisplayCategory(category2.getCategoryId(), shopId, TAKE_OUT, 2, true);
    createDisplayCategory(category3.getCategoryId(), shopId, TAKE_OUT, 3, true);

    OrderCategoryOrderingSaveServiceRequest request = OrderCategoryOrderingSaveServiceRequest.builder()
        .displayMappingType(SHOP)
        .categories(List.of(
            MappingCategoryServiceDto.builder()
                .id(category2.getCategoryId())
                .ordering(1)
                .build(),
            MappingCategoryServiceDto.builder()
                .id(category3.getCategoryId())
                .ordering(2)
                .build(),
            MappingCategoryServiceDto.builder()
                .id(category1.getCategoryId())
                .ordering(3)
                .build()
        ))
        .build();

    // when
    OrderCategoryOrderingServiceResponse result = orderMenuMappingApiService.saveCategoryOrdering(
        shopId, request);

    // then
    assertThat(result.getCategories()).hasSize(3)
        .extracting("id", "ordering")
        .containsExactly(
            tuple(category2.getCategoryId(), 1),
            tuple(category3.getCategoryId(), 2),
            tuple(category1.getCategoryId(), 3)
        );

    assertThat(displayCategoryRepository.findAll()).hasSize(6)
        .extracting("categoryId", "displayMappingType", "ordering")
        .contains(
            tuple(category1.getCategoryId(), SHOP, 3),
            tuple(category1.getCategoryId(), TAKE_OUT, 1),
            tuple(category2.getCategoryId(), SHOP, 1),
            tuple(category2.getCategoryId(), TAKE_OUT, 2),
            tuple(category3.getCategoryId(), SHOP, 2),
            tuple(category3.getCategoryId(), TAKE_OUT, 3)
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

  private MenuEntity createMenu(String shopId, String name, int ordering, long unitPrice) {
    MenuEntity menu = MenuEntity.builder()
        .menuId(UUIDUtil.shortUUID())
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .unitPrice(Price.of(unitPrice))
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

  private DisplayCategoryEntity createDisplayCategory(String categoryId, String shopId,
      DisplayMappingType displayMappingType, int ordering, boolean isAllChecked) {
    DisplayCategoryEntity displayCategory = DisplayCategoryEntity.builder()
        .categoryId(categoryId)
        .shopId(shopId)
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .isAllChecked(isAllChecked)
        .build();
    return displayCategoryRepository.save(displayCategory);
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

  private DisplayMenuEntity createDisplayMenu(String categoryId, MenuEntity menuEntity,
      DisplayMappingType displayMappingType, int ordering, boolean isChecked) {
    DisplayMenuEntity displayMenu = DisplayMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(menuEntity.getMenuId())
        .shopId(menuEntity.getShopId())
        .menuName(menuEntity.getName())
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .unitPrice(menuEntity.getUnitPrice())
        .isChecked(isChecked)
        .build();
    return displayMenuRepository.save(displayMenu);
  }

  private DisplayMenuEntity createDisplayMenu(String categoryId, String menuId, String shopId,
      DisplayMappingType displayMappingType, int ordering, String menuName, long unitPrice,
      boolean isChecked
  ) {
    DisplayMenuEntity displayMenu = DisplayMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(menuId)
        .shopId(shopId)
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .menuName(menuName)
        .unitPrice(Price.of(BigDecimal.valueOf(unitPrice)))
        .isChecked(isChecked)
        .build();
    return displayMenuRepository.save(displayMenu);
  }

}