package co.wadcorp.waiting.data.query.menu;

import static org.assertj.core.api.Assertions.assertThat;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
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
class MenuQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private MenuQueryRepository menuQueryRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private CategoryMenuRepository categoryMenuRepository;

  @DisplayName("메뉴 ID에 매핑된 카테고리 ID를 조회한다.")
  @Test
  void findCategoryIdBy() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());

    // when
    String categoryId = menuQueryRepository.findCategoryIdBy(menu1.getMenuId());

    // then
    assertThat(categoryId).isEqualTo(category1.getCategoryId());
  }

  @DisplayName("특정 카테고리 ID에 매핑된 모든 메뉴를 조회한다.")
  @Test
  void findAllByCategoryId() {
    // given
    String shopId = "shopId";

    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);

    MenuEntity menu1 = createMenu(shopId, "메뉴1", 1, 1000);
    MenuEntity menu2 = createMenu(shopId, "메뉴2", 3, 3000);
    MenuEntity menu3 = createMenu(shopId, "메뉴3", 2, 2000);

    createCategoryMenu(category1.getCategoryId(), menu1.getMenuId());
    createCategoryMenu(category1.getCategoryId(), menu2.getMenuId());
    createCategoryMenu(category2.getCategoryId(), menu3.getMenuId());

    // when
    List<MenuEntity> results = menuQueryRepository.findAllByCategoryId(
        category1.getCategoryId());

    // then
    assertThat(results).hasSize(2)
        .extracting("name")
        .contains("메뉴1", "메뉴2");
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

}