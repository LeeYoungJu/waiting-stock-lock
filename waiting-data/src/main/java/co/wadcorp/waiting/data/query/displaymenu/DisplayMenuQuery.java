package co.wadcorp.waiting.data.query.displaymenu;

import static co.wadcorp.waiting.data.domain.displaymenu.QDisplayMenuEntity.displayMenuEntity;
import static co.wadcorp.waiting.data.domain.menu.QMenuEntity.menuEntity;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class DisplayMenuQuery {

  private final JPAQueryFactory queryFactory;

  public List<DisplayMenuDto> getDisplayMenu(String shopId, DisplayMappingType displayMappingType) {
    return queryFactory.select(Projections.fields(
            DisplayMenuDto.class,
            displayMenuEntity.categoryId,
            menuEntity.menuId,
            menuEntity.name.as("menuName"),
            displayMenuEntity.ordering,
            displayMenuEntity.displayMappingType,
            menuEntity.unitPrice,
            menuEntity.isUsedDailyStock,
            menuEntity.dailyStock,
            menuEntity.isUsedMenuQuantityPerTeam,
            menuEntity.menuQuantityPerTeam))
        .from(menuEntity)
        .join(displayMenuEntity).on(displayMenuEntity.menuId.eq(menuEntity.menuId))
        .where(
            menuEntity.shopId.eq(shopId),
            displayMenuEntity.displayMappingType.eq(displayMappingType),
            displayMenuEntity.isChecked.eq(true)
        )
        .fetch();
  }
}
