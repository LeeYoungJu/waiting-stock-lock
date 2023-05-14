package co.wadcorp.waiting.data.query.displaymenu;

import static co.wadcorp.waiting.data.domain.displaymenu.QDisplayCategoryEntity.displayCategoryEntity;
import static co.wadcorp.waiting.data.domain.menu.QCategoryEntity.categoryEntity;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class DisplayCategoryQuery {

  private final JPAQueryFactory queryFactory;

  public List<DisplayCategoryDto> getDisplayCategory(
      Set<String> categoryIds,
      DisplayMappingType displayMappingType
  ) {
    return queryFactory.select(Projections.fields(
            DisplayCategoryDto.class,
            displayCategoryEntity.categoryId,
            categoryEntity.name.as("categoryName"),
            displayCategoryEntity.ordering,
            displayCategoryEntity.displayMappingType,
            displayCategoryEntity.isAllChecked
        ))
        .from(categoryEntity)
        .join(displayCategoryEntity)
        .on(displayCategoryEntity.categoryId.eq(categoryEntity.categoryId))
        .where(
            categoryEntity.categoryId.in(categoryIds),
            displayCategoryEntity.displayMappingType.eq(displayMappingType)
        )
        .orderBy(displayCategoryEntity.ordering.asc())
        .fetch();
  }
}
