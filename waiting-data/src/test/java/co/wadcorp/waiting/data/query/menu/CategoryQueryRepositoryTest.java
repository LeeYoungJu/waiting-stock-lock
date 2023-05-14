package co.wadcorp.waiting.data.query.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import co.wadcorp.libs.util.UUIDUtil;
import co.wadcorp.waiting.IntegrationTest;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CategoryQueryRepositoryTest extends IntegrationTest {

  @Autowired
  private CategoryQueryRepository categoryQueryRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @DisplayName("shopId에 해당하는 모든 카테고리를 조회한다.")
  @Test
  void findAllBy() {
    // given
    String shopId = "shopId";

    createCategory(shopId, "카테고리1", 1);
    createCategory(shopId, "카테고리2", 2);

    // when
    List<CategoryEntity> results = categoryQueryRepository.findAllBy(shopId);

    // then
    assertThat(results).hasSize(2)
        .extracting("name", "ordering")
        .containsExactlyInAnyOrder(
            tuple("카테고리1", 1),
            tuple("카테고리2", 2)
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

}