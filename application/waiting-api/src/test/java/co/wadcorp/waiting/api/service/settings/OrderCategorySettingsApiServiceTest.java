package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.SHOP;
import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.TAKE_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest.OrderCategoryServiceDto;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsResponse;
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
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OrderCategorySettingsApiServiceTest extends IntegrationTest {

  @Autowired
  private OrderCategorySettingsApiService orderCategorySettingsApiService;

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

  @DisplayName("신규 단건 카테고리를 저장한다. 순서는 (마지막순서 + 1)이다.")
  @Test
  void saveCategory() {
    // given
    String shopId = "shopId";
    String newCategoryId = UUIDUtil.shortUUID();

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 2);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 1);
    createDisplayCategory(category1, SHOP, 1);
    createDisplayCategory(category1, TAKE_OUT, 1);
    createDisplayCategory(category2, SHOP, 2);
    createDisplayCategory(category2, TAKE_OUT, 2);

    OrderCategorySettingsServiceRequest request = OrderCategorySettingsServiceRequest.builder()
        .id(newCategoryId)
        .name("카테고리3")
        .build();

    // when
    OrderCategorySettingsResponse result = orderCategorySettingsApiService.saveCategory(
        shopId, request);

    // then
    assertThat(result)
        .extracting("id", "name", "ordering")
        .containsExactly(newCategoryId, "카테고리3", 3);

    List<DisplayCategoryEntity> displayCategories = displayCategoryRepository.findAll();
    assertThat(displayCategories).hasSize(6)
        .extracting("categoryId", "displayMappingType", "ordering", "isAllChecked")
        .contains(
            tuple(category1.getCategoryId(), SHOP, 1, false),
            tuple(category1.getCategoryId(), TAKE_OUT, 1, false),
            tuple(category2.getCategoryId(), SHOP, 2, false),
            tuple(category2.getCategoryId(), TAKE_OUT, 2, false),
            tuple(newCategoryId, SHOP, 3, false),
            tuple(newCategoryId, TAKE_OUT, 3, false)
        );
  }

  @DisplayName("신규 단건 카테고리 첫 등록 시 순서는 1이다.")
  @Test
  void saveCategoryFirstTime() {
    // given
    String shopId = "shopId";
    String newCategoryId = UUIDUtil.shortUUID();

    OrderCategorySettingsServiceRequest request = OrderCategorySettingsServiceRequest.builder()
        .id(newCategoryId)
        .name("카테고리1")
        .build();

    // when
    OrderCategorySettingsResponse result = orderCategorySettingsApiService.saveCategory(
        shopId, request);

    // then
    assertThat(result)
        .extracting("id", "name", "ordering")
        .containsExactly(newCategoryId, "카테고리1", 1);

    List<DisplayCategoryEntity> displayCategories = displayCategoryRepository.findAll();
    assertThat(displayCategories).hasSize(2)
        .extracting("categoryId", "displayMappingType", "ordering", "isAllChecked")
        .contains(
            tuple(newCategoryId, SHOP, 1, false),
            tuple(newCategoryId, TAKE_OUT, 1, false)
        );
  }

  @DisplayName("카테고리 리스트를 조회하면 ordering 순서대로 응답한다.")
  @Test
  void getCategories() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 2);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 3);
    CategoryEntity category3 = createCategory(shopId, "카테고리3", 1);

    // when
    OrderCategorySettingsListResponse result = orderCategorySettingsApiService.getCategories(
        shopId);

    // then
    assertThat(result.categories()).hasSize(3)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(category3.getCategoryId(), "카테고리3", 1),
            tuple(category1.getCategoryId(), "카테고리1", 2),
            tuple(category2.getCategoryId(), "카테고리2", 3)
        );
  }

  @DisplayName("카테고리 리스트 저장 시 생성, 수정, 삭제를 일괄 처리한다.")
  @Test
  void saveAllCategories() {
    // given
    String shopId = "shopId";
    String newCategoryId = UUIDUtil.shortUUID();

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 2); // 수정 대상
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 1); // 삭제 대상
    createDisplayCategory(category1, SHOP, 1);
    createDisplayCategory(category1, TAKE_OUT, 1);
    createDisplayCategory(category2, SHOP, 2);
    createDisplayCategory(category2, TAKE_OUT, 2);

    OrderCategorySettingsListServiceRequest request = OrderCategorySettingsListServiceRequest.builder()
        .categories(List.of(
            OrderCategoryServiceDto.builder()
                .id(category1.getCategoryId())
                .name(category1.getName())
                .ordering(1)
                .build(),
            OrderCategoryServiceDto.builder() // 생성
                .id(newCategoryId)
                .name("카테고리3")
                .ordering(2)
                .build()
        ))
        .build();

    // when
    OrderCategorySettingsListResponse result = orderCategorySettingsApiService.saveAllCategories(
        shopId, request);

    // then
    assertThat(result.categories()).hasSize(2)
        .extracting("id", "name", "ordering")
        .containsExactly(
            tuple(category1.getCategoryId(), "카테고리1", 1),
            tuple(newCategoryId, "카테고리3", 2)
        );

    assertThat(categoryRepository.findAll()).hasSize(3)
        .extracting("categoryId", "name", "ordering", "isDeleted")
        .containsExactly(
            tuple(category1.getCategoryId(), "카테고리1", 1, false),
            tuple(category2.getCategoryId(), "카테고리2", 1, true),
            tuple(newCategoryId, "카테고리3", 2, false)
        );

    List<DisplayCategoryEntity> displayCategories = displayCategoryRepository.findAll();
    assertThat(displayCategories).hasSize(4)
        .extracting("categoryId", "displayMappingType", "ordering", "isAllChecked")
        .contains(
            tuple(category1.getCategoryId(), SHOP, 1, false),
            tuple(category1.getCategoryId(), TAKE_OUT, 1, false),
            tuple(newCategoryId, SHOP, 3, false),
            tuple(newCategoryId, TAKE_OUT, 3, false)
        );
  }

  @DisplayName("카테고리 리스트 저장 시 삭제된 카테고리에 매핑되어 있는 메뉴도 삭제한다.")
  @Test
  void deleteMenusWhenCategoryIsDeleted() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    createDisplayCategory(category1, SHOP, 1);
    createDisplayCategory(category1, TAKE_OUT, 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2, 2000);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3, 3000);
    createDisplayMenu(category1.getCategoryId(), menu1, SHOP, 2);
    createDisplayMenu(category1.getCategoryId(), menu1, TAKE_OUT, 2);
    createDisplayMenu(category1.getCategoryId(), menu2, SHOP, 1);
    createDisplayMenu(category1.getCategoryId(), menu2, TAKE_OUT, 1);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());

    OrderCategorySettingsListServiceRequest request = OrderCategorySettingsListServiceRequest.builder()
        .categories(List.of())
        .build();

    // when
    OrderCategorySettingsListResponse result = orderCategorySettingsApiService.saveAllCategories(
        shopId, request);

    // then
    assertThat(result.categories()).isEmpty();

    assertThat(menuRepository.findAll()).hasSize(3)
        .extracting("menuId", "name", "isDeleted")
        .containsExactly(
            tuple(menu1.getMenuId(), "메뉴1", true),
            tuple(menu2.getMenuId(), "메뉴2", true),
            tuple(menu3.getMenuId(), "메뉴3", false)
        );

    assertThat(displayCategoryRepository.findAll()).isEmpty();
    assertThat(displayMenuRepository.findAll()).isEmpty();
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

  private DisplayCategoryEntity createDisplayCategory(CategoryEntity category,
      DisplayMappingType displayMappingType, int ordering) {
    DisplayCategoryEntity displayCategory = DisplayCategoryEntity.builder()
        .categoryId(category.getCategoryId())
        .shopId(category.getShopId())
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .isAllChecked(false)
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

}