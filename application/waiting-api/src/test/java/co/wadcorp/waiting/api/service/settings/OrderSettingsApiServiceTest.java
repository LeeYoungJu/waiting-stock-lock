package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.SHOP;
import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.TAKE_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderSettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderSettingsResponse;
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
import co.wadcorp.waiting.data.domain.settings.OrderSettingsData;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.OrderSettingsRepository;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoRepository;
import co.wadcorp.waiting.data.support.Price;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OrderSettingsApiServiceTest extends IntegrationTest {

  @Autowired
  private OrderSettingsApiService orderSettingsApiService;

  @Autowired
  private OrderSettingsRepository orderSettingsRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CategoryMenuRepository categoryMenuRepository;

  @Autowired
  private DisplayCategoryRepository displayCategoryRepository;

  @Autowired
  private DisplayMenuRepository displayMenuRepository;

  @Autowired
  private ShopOperationInfoRepository shopOperationInfoRepository;

  @DisplayName("주문 설정을 조회한다. 각 메뉴는 카테고리-메뉴 순서에 맞게 정렬된다.")
  @Test
  void getOrderSettings() {
    // given
    String shopId = "shopId";
    boolean isPossibleOrder = true;
    LocalDate operationDate = LocalDate.of(2023, 4, 3);
    ZonedDateTime nowDateTime = ZonedDateTime.of(operationDate, LocalTime.of(13, 0, 0), ZoneId.of("Asia/Seoul"));

    shopOperationInfoRepository.save(ShopOperationInfoEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .operationStartDateTime(ZonedDateTime.of(operationDate, LocalTime.of(12, 0, 0), ZoneId.of("Asia/Seoul")))
        .operationEndDateTime(ZonedDateTime.of(operationDate, LocalTime.of(14, 0, 0), ZoneId.of("Asia/Seoul")))
        .registrableStatus(RegistrableStatus.OPEN)
        .build());

    createOrderSettings(shopId, isPossibleOrder);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1000L, 2);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2000L, 1);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3000L, 3);
    MenuEntity menu4 = createMenu(shopId, "메뉴4", 4000L, 5);
    MenuEntity menu5 = createMenu(shopId, "메뉴5", 5000L, 4);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu3.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu4.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu5.getMenuId());

    createDisplayCategory(category1, SHOP, 2);
    createDisplayCategory(category1, TAKE_OUT, 1);
    createDisplayCategory(category2, SHOP, 1);
    createDisplayCategory(category2, TAKE_OUT, 2);

    createDisplayMenu(menu1, category1.getCategoryId(), DisplayMappingType.SHOP, 2);
    createDisplayMenu(menu2, category1.getCategoryId(), DisplayMappingType.SHOP, 1);
    createDisplayMenu(menu3, category1.getCategoryId(), DisplayMappingType.SHOP, 4);
    createDisplayMenu(menu4, category2.getCategoryId(), DisplayMappingType.SHOP, 3);
    createDisplayMenu(menu5, category2.getCategoryId(), DisplayMappingType.SHOP, 5);

    createDisplayMenu(menu1, category1.getCategoryId(), DisplayMappingType.TAKE_OUT, 5);
    createDisplayMenu(menu2, category1.getCategoryId(), DisplayMappingType.TAKE_OUT, 4);
    createDisplayMenu(menu3, category1.getCategoryId(), DisplayMappingType.TAKE_OUT, 3);
    createDisplayMenu(menu4, category2.getCategoryId(), DisplayMappingType.TAKE_OUT, 2);
    createDisplayMenu(menu5, category2.getCategoryId(), DisplayMappingType.TAKE_OUT, 1);

    // when
    OrderSettingsResponse orderSettings = orderSettingsApiService.getOrderSettings(shopId, operationDate, nowDateTime);

    // then
    assertThat(orderSettings.getIsPossibleOrder()).isEqualTo(isPossibleOrder);

    assertThat(orderSettings.getMenus()).hasSize(5)
        .extracting("name")
        .containsExactly(menu2.getName(), menu1.getName(), menu3.getName(), menu5.getName(),
            menu4.getName()
        );

    assertThat(orderSettings.getShopMenus()).hasSize(5)
        .extracting("name")
        .containsExactly(menu4.getName(), menu5.getName(), menu2.getName(), menu1.getName(),
            menu3.getName()
        );

    assertThat(orderSettings.getTakeOutMenus()).hasSize(5)
        .extracting("name")
        .containsExactly(menu3.getName(), menu2.getName(), menu1.getName(), menu5.getName(),
            menu4.getName()
        );
  }

  @DisplayName("주문 설정을 저장하고 기존 설정 정보는 unpublish 처리한다. 각 메뉴는 순서에 맞게 정렬된다.")
  @Test
  void saveOrderSettings() {
    // given
    String shopId = "shopId";
    boolean isPossibleOrder = false;
    LocalDate operationDate = LocalDate.of(2023, 4, 3);
    ZonedDateTime nowDateTime = ZonedDateTime.of(operationDate, LocalTime.of(13, 0, 0), ZoneId.of("Asia/Seoul"));

    shopOperationInfoRepository.save(ShopOperationInfoEntity.builder()
        .shopId(shopId)
        .operationDate(operationDate)
        .operationStartDateTime(ZonedDateTime.of(operationDate, LocalTime.of(12, 0, 0), ZoneId.of("Asia/Seoul")))
        .operationEndDateTime(ZonedDateTime.of(operationDate, LocalTime.of(14, 0, 0), ZoneId.of("Asia/Seoul")))
        .registrableStatus(RegistrableStatus.OPEN)
        .build());

    createOrderSettings(shopId, isPossibleOrder);

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1000L, 2);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 2000L, 1);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 3000L, 3);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu3.getMenuId());

    createDisplayCategory(category1, SHOP, 2);
    createDisplayCategory(category1, TAKE_OUT, 1);

    createDisplayMenu(menu1, category1.getCategoryId(), DisplayMappingType.SHOP, 2);
    createDisplayMenu(menu2, category1.getCategoryId(), DisplayMappingType.SHOP, 1);

    createDisplayMenu(menu1, category1.getCategoryId(), DisplayMappingType.TAKE_OUT, 2);
    createDisplayMenu(menu3, category1.getCategoryId(), DisplayMappingType.TAKE_OUT, 1);

    OrderSettingsServiceRequest request = OrderSettingsServiceRequest.builder()
        .isPossibleOrder(true)
        .build();

    // when
    OrderSettingsResponse orderSettings = orderSettingsApiService.saveOrderSettings(shopId,
        request, operationDate, nowDateTime);

    // then
    assertThat(orderSettings.getIsPossibleOrder()).isTrue();
    assertThat(orderSettingsRepository.findAll()).hasSize(2)
        .extracting("orderSettingsData.isPossibleOrder", "isPublished")
        .contains(
            tuple(false, false),
            tuple(true, true)
        );

    assertThat(orderSettings.getMenus()).hasSize(3)
        .extracting("name")
        .containsExactly(menu2.getName(), menu1.getName(), menu3.getName());

    assertThat(orderSettings.getShopMenus()).hasSize(2)
        .extracting("name")
        .containsExactly(menu2.getName(), menu1.getName());

    assertThat(orderSettings.getTakeOutMenus()).hasSize(2)
        .extracting("name")
        .containsExactly(menu3.getName(), menu1.getName());
  }

  private OrderSettingsEntity createOrderSettings(String shopId, boolean isPossibleOrder) {
    OrderSettingsEntity orderSettings = OrderSettingsEntity.builder()
        .shopId(shopId)
        .orderSettingsData(OrderSettingsData.builder()
            .isPossibleOrder(isPossibleOrder)
            .build()
        )
        .build();
    return orderSettingsRepository.save(orderSettings);
  }

  private MenuEntity createMenu(String shopId, String name, long price, int ordering) {
    MenuEntity menu = MenuEntity.builder()
        .menuId(UUIDUtil.shortUUID())
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .unitPrice(Price.of(price))
        .build();
    return menuRepository.save(menu);
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

  private CategoryMenuEntity createCategoryMenu(String categoryId, String menuId) {
    CategoryMenuEntity categoryMenu = CategoryMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(menuId)
        .build();
    return categoryMenuRepository.save(categoryMenu);
  }

  private DisplayMenuEntity createDisplayMenu(MenuEntity menuEntity, String categoryId,
      DisplayMappingType displayMappingType, int ordering) {
    DisplayMenuEntity displayMenu = DisplayMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(menuEntity.getMenuId())
        .shopId(menuEntity.getShopId())
        .menuName(menuEntity.getName())
        .displayMappingType(displayMappingType)
        .ordering(ordering)
        .unitPrice(menuEntity.getUnitPrice())
        .isChecked(true)
        .build();
    return displayMenuRepository.save(displayMenu);
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

}