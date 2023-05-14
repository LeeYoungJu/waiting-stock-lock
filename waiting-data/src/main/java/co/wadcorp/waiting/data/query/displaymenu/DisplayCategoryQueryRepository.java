package co.wadcorp.waiting.data.query.displaymenu;

import static co.wadcorp.waiting.data.domain.displaymenu.QDisplayCategoryEntity.displayCategoryEntity;
import static co.wadcorp.waiting.data.domain.menu.QCategoryEntity.categoryEntity;
import static com.querydsl.core.types.Projections.fields;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class DisplayCategoryQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<DisplayCategoryDto> findDisplayCategoriesBy(String shopId,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(fields(
            DisplayCategoryDto.class,
            displayCategoryEntity.seq,
            displayCategoryEntity.categoryId,
            categoryEntity.name.as("categoryName"),
            displayCategoryEntity.ordering,
            displayCategoryEntity.displayMappingType,
            displayCategoryEntity.isAllChecked
        ))
        .from(displayCategoryEntity)
        .innerJoin(categoryEntity)
        .on(displayCategoryEntity.categoryId.eq(categoryEntity.categoryId))
        .where(
            displayCategoryEntity.shopId.eq(shopId),
            displayCategoryEntity.displayMappingType.eq(displayMappingType)
        )
        .fetch();
  }

  public List<DisplayCategoryDto> findDisplayCategoriesByCategoryIds(List<String> categoryIds,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(fields(
            DisplayCategoryDto.class,
            displayCategoryEntity.seq,
            displayCategoryEntity.categoryId,
            categoryEntity.name.as("categoryName"),
            displayCategoryEntity.ordering,
            displayCategoryEntity.displayMappingType,
            displayCategoryEntity.isAllChecked
        ))
        .from(displayCategoryEntity)
        .innerJoin(categoryEntity)
        .on(displayCategoryEntity.categoryId.eq(categoryEntity.categoryId))
        .where(
            displayCategoryEntity.categoryId.in(categoryIds),
            displayCategoryEntity.displayMappingType.eq(displayMappingType)
        )
        .fetch();
  }

  public List<DisplayCategoryEntity> findAllBy(String shopId,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(displayCategoryEntity)
        .from(displayCategoryEntity)
        .where(
            displayCategoryEntity.shopId.eq(shopId),
            displayCategoryEntity.displayMappingType.eq(displayMappingType)
        )
        .fetch();
  }

  public boolean findAllCheckedByCategoryId(String categoryId,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(displayCategoryEntity.isAllChecked)
        .from(displayCategoryEntity)
        .where(
            displayCategoryEntity.categoryId.eq(categoryId),
            displayCategoryEntity.displayMappingType.eq(displayMappingType)
        )
        .fetchFirst();
  }

}
