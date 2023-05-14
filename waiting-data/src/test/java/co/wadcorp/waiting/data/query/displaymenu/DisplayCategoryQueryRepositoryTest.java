package co.wadcorp.waiting.data.query.displaymenu;

import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.SHOP;
import static co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType.TAKE_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryRepository;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryRepository;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DisplayCategoryQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private DisplayCategoryQueryRepository displayCategoryQueryRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private DisplayCategoryRepository displayCategoryRepository;

  @DisplayName("매장 ID와 메뉴판 타입으로 전체 카테고리를 조회한다.")
  @Test
  void findDisplayCategoriesBy() {
    // given
    String shopId = "shopId";
    CategoryEntity category1 = createCategory(shopId, "카테고리1", 1);
    CategoryEntity category2 = createCategory(shopId, "카테고리2", 2);
    CategoryEntity category3 = createCategory("shopId-2", "카테고리3", 3);

    createDisplayCategory(category1.getCategoryId(), shopId, SHOP, 1, true);
    createDisplayCategory(category1.getCategoryId(), shopId, TAKE_OUT, 1, false);
    createDisplayCategory(category2.getCategoryId(), shopId, SHOP, 2, false);
    createDisplayCategory(category2.getCategoryId(), shopId, TAKE_OUT, 2, false);
    createDisplayCategory(category3.getCategoryId(), "shopId-2", SHOP, 1, false);

    // when
    List<DisplayCategoryDto> results = displayCategoryQueryRepository.findDisplayCategoriesBy(
        shopId, SHOP);

    // then
    assertThat(results).hasSize(2)
        .extracting("categoryId", "categoryName", "ordering", "isAllChecked")
        .contains(
            tuple(category1.getCategoryId(), "카테고리1", 1, true),
            tuple(category2.getCategoryId(), "카테고리2", 2, false)
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

}