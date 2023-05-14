package co.wadcorp.waiting.api.internal.service.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.wadcorp.waiting.api.IntegrationTest;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse.CategoryDto;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse.MenuDto;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
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
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RemoteDisplayMenuApiServiceTest extends IntegrationTest {

  @Autowired
  private RemoteDisplayMenuApiService remoteDisplayMenuApiService;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private DisplayCategoryRepository displayCategoryRepository;
  @Autowired
  private MenuRepository menuRepository;
  @Autowired
  private DisplayMenuRepository displayMenuRepository;
  @Autowired
  private StockRepository stockRepository;

  @DisplayName("특정 매장의 주문 메뉴 정보를 조회할 수 있다.")
  @Test
  void getOrderMenu() {
    // given
    String shopId = "shopId";
    String categoryId1 = "CATEGORY_ID_1";
    String categoryId2 = "CATEGORY_ID_2";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "111");

    DisplayMappingType displayMappingType = DisplayMappingType.SHOP;
    LocalDate operationDate = LocalDate.of(2023, 5, 4);

    CategoryEntity category1 = CategoryEntity.builder()
        .shopId(shopId)
        .categoryId(categoryId1)
        .name("음식")
        .ordering(1)
        .build();
    CategoryEntity category2 = CategoryEntity.builder()
        .shopId(shopId)
        .categoryId(categoryId2)
        .name("음료")
        .ordering(2)
        .build();

    DisplayCategoryEntity displayCategory1 = DisplayCategoryEntity.of(category1, displayMappingType, 1);
    DisplayCategoryEntity displayCategory2 = DisplayCategoryEntity.of(category2, displayMappingType, 2);

    MenuEntity menu1 = MenuEntity.builder()
        .shopId(shopId)
        .menuId("MENU_ID_1")
        .name("돈까스")
        .ordering(1)
        .unitPrice(Price.of(10000))
        .isUsedDailyStock(true)
        .dailyStock(3)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();

    MenuEntity menu2 = MenuEntity.builder()
        .shopId(shopId)
        .menuId("MENU_ID_2")
        .name("냉면")
        .ordering(2)
        .unitPrice(Price.of(4000))
        .isUsedDailyStock(true)
        .dailyStock(20)
        .isUsedMenuQuantityPerTeam(false)
        .build();

    MenuEntity menu3 = MenuEntity.builder()
        .shopId(shopId)
        .menuId("MENU_ID_3")
        .name("콜라")
        .ordering(3)
        .unitPrice(Price.of(1200))
        .isUsedDailyStock(true)
        .dailyStock(1000)
        .isUsedMenuQuantityPerTeam(false)
        .build();

    DisplayMenuEntity displayMenu1 = DisplayMenuEntity.of(menu1, categoryId1, displayMappingType, 1);
    DisplayMenuEntity displayMenu2 = DisplayMenuEntity.of(menu2, categoryId1, displayMappingType, 2);
    DisplayMenuEntity displayMenu3 = DisplayMenuEntity.of(menu3, categoryId2, displayMappingType, 1);

    saveCategories(List.of(category1, category2),
        List.of(displayCategory1, displayCategory2));
    saveMenus(List.of(menu1, menu2, menu3),
        List.of(displayMenu1, displayMenu2, displayMenu3),
        operationDate);

    // when
    RemoteWaitingOrderMenuResponse response = remoteDisplayMenuApiService.getOrderMenu(
        channelShopIdMapping,
        displayMappingType,
        operationDate
    );

    // then
    assertEquals(2, response.getCategories().size());
    CategoryDto resultCategory1 = response.getCategories().stream()
        .filter(categoryDto -> categoryDto.getId().equals(categoryId1))
        .findFirst()
        .get();
    CategoryDto resultCategory2 = response.getCategories().stream()
        .filter(categoryDto -> categoryDto.getId().equals(categoryId2))
        .findFirst()
        .get();
    List<MenuDto> category1Menus = resultCategory1.getMenus();
    List<MenuDto> category2Menus = resultCategory2.getMenus();
    assertThat(category1Menus).hasSize(2)
        .extracting("name")
        .containsExactly(menu1.getName(), menu2.getName());
    assertThat(category2Menus).hasSize(1)
        .extracting("name")
        .containsExactly(menu3.getName());
  }

  @DisplayName("주문 메뉴 조회 시 메뉴가 없으면 빈 배열을 제공한다.")
  @Test
  void getOrderEmptyMenu() {
    // given
    String shopId = "shopId";
    String categoryId1 = "CATEGORY_ID_1";
    String categoryId2 = "CATEGORY_ID_2";
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    channelShopIdMapping.put(shopId, "111");

    DisplayMappingType displayMappingTypeRequest = DisplayMappingType.TAKE_OUT;  // 매장에는 포장용 메뉴가 없는데 요청을 한 경우
    DisplayMappingType displayMappingType = DisplayMappingType.SHOP;
    LocalDate operationDate = LocalDate.of(2023, 5, 4);

    CategoryEntity category1 = CategoryEntity.builder()
        .shopId(shopId)
        .categoryId(categoryId1)
        .name("음식")
        .ordering(1)
        .build();

    DisplayCategoryEntity displayCategory1 = DisplayCategoryEntity.of(category1, displayMappingType, 1);

    MenuEntity menu1 = MenuEntity.builder()
        .shopId(shopId)
        .menuId("MENU_ID_1")
        .name("돈까스")
        .ordering(1)
        .unitPrice(Price.of(10000))
        .isUsedDailyStock(true)
        .dailyStock(3)
        .isUsedMenuQuantityPerTeam(true)
        .menuQuantityPerTeam(2)
        .build();

    DisplayMenuEntity displayMenu1 = DisplayMenuEntity.of(menu1, categoryId1, displayMappingType, 1);

    saveCategories(List.of(category1), List.of(displayCategory1));
    saveMenus(List.of(menu1),
        List.of(displayMenu1),
        operationDate);

    // when
    RemoteWaitingOrderMenuResponse response = remoteDisplayMenuApiService.getOrderMenu(
        channelShopIdMapping,
        displayMappingTypeRequest,
        operationDate
    );

    // then
    // 만약 매장 설정에 현장용 메뉴만 있는데 포장용 메뉴를 요청하면 빈 배열을 반환한다.
    assertEquals(0, response.getCategories().size());
  }

  private void saveCategories(List<CategoryEntity> categories, List<DisplayCategoryEntity> displayCategories) {
    categories.forEach(categoryEntity -> categoryRepository.save(categoryEntity));
    displayCategories.forEach(displayCategoryEntity -> displayCategoryRepository.save(displayCategoryEntity));
  }

  private void saveMenus(List<MenuEntity> menus, List<DisplayMenuEntity> displayMenus, LocalDate operationDate) {
    menus.forEach(menuEntity -> {
      menuRepository.save(menuEntity);
      stockRepository.save(StockEntity.builder()
          .menuId(menuEntity.getMenuId())
          .operationDate(operationDate)
          .isUsedDailyStock(true)
          .stock(100)
          .salesQuantity(0)
          .build());
    });
    displayMenus.forEach(displayMenuEntity -> {
      displayMenuEntity.checked();
      displayMenuRepository.save(displayMenuEntity);
    });
  }
}