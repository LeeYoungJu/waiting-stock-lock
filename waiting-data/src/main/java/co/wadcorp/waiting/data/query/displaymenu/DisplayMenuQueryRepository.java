package co.wadcorp.waiting.data.query.displaymenu;

import static co.wadcorp.waiting.data.domain.displaymenu.QDisplayCategoryEntity.displayCategoryEntity;
import static co.wadcorp.waiting.data.domain.displaymenu.QDisplayMenuEntity.displayMenuEntity;
import static com.querydsl.core.types.Projections.fields;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuNamePriceDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class DisplayMenuQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<DisplayMenuEntity> findAllBy(String categoryId,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(displayMenuEntity)
        .from(displayMenuEntity)
        .where(
            displayMenuEntity.categoryId.eq(categoryId),
            displayMenuEntity.displayMappingType.eq(displayMappingType)
        )
        .fetch();
  }

  public List<DisplayMenuEntity> findAllBy(List<String> categoryIds,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(displayMenuEntity)
        .from(displayMenuEntity)
        .where(
            displayMenuEntity.categoryId.in(categoryIds),
            displayMenuEntity.displayMappingType.eq(displayMappingType)
        )
        .fetch();
  }

  public List<DisplayMenuEntity> findAllByShopId(String shopId,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(displayMenuEntity)
        .from(displayMenuEntity)
        .where(
            displayMenuEntity.shopId.in(shopId),
            displayMenuEntity.displayMappingType.eq(displayMappingType)
        )
        .fetch();
  }

  public List<DisplayMenuNamePriceDto> findDisplayMenusBy(String shopId,
      DisplayMappingType displayMappingType) {
    return queryFactory
        .select(fields(
            DisplayMenuNamePriceDto.class,
            displayMenuEntity.seq,
            displayMenuEntity.categoryId,
            displayCategoryEntity.ordering.as("categoryOrdering"),
            displayMenuEntity.menuId,
            displayMenuEntity.menuName.as("name"),
            displayMenuEntity.unitPrice,
            displayMenuEntity.ordering.as("menuOrdering"),
            displayMenuEntity.isChecked
        ))
        .from(displayMenuEntity)
        .innerJoin(displayCategoryEntity)
        .on(displayMenuEntity.categoryId.eq(displayCategoryEntity.categoryId))
        .where(
            displayMenuEntity.shopId.eq(shopId),
            displayMenuEntity.displayMappingType.eq(displayMappingType),
            displayCategoryEntity.displayMappingType.eq(displayMappingType)
        )
        .fetch();
  }

}
