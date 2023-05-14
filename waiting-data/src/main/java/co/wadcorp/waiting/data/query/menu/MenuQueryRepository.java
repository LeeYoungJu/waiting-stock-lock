package co.wadcorp.waiting.data.query.menu;

import static co.wadcorp.waiting.data.domain.menu.QCategoryEntity.categoryEntity;
import static co.wadcorp.waiting.data.domain.menu.QCategoryMenuEntity.categoryMenuEntity;
import static co.wadcorp.waiting.data.domain.menu.QMenuEntity.menuEntity;
import static com.querydsl.core.types.Projections.fields;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.query.menu.dto.MenuNamePriceDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class MenuQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<MenuNamePriceDto> findMenuDtosBy(String shopId) {
    return queryFactory
        .select(fields(
            MenuNamePriceDto.class,
            menuEntity.seq,
            categoryEntity.categoryId,
            categoryEntity.ordering.as("categoryOrdering"),
            menuEntity.menuId,
            menuEntity.name,
            menuEntity.unitPrice,
            menuEntity.ordering.as("menuOrdering")
        ))
        .from(menuEntity)
        .innerJoin(categoryMenuEntity)
        .on(menuEntity.menuId.eq(categoryMenuEntity.menuId))
        .innerJoin(categoryEntity)
        .on(categoryMenuEntity.categoryId.eq(categoryEntity.categoryId))
        .where(
            menuEntity.shopId.eq(shopId),
            menuEntity.isDeleted.isFalse()
        )
        .fetch();
  }

  public List<MenuEntity> findAllBy(String shopId) {
    return queryFactory
        .select(menuEntity)
        .from(menuEntity)
        .where(
            menuEntity.shopId.eq(shopId),
            menuEntity.isDeleted.isFalse()
        )
        .fetch();
  }

  public List<String> findAllMenuIdsBy(String shopId) {
    return queryFactory
        .select(menuEntity.menuId)
        .from(menuEntity)
        .where(
            menuEntity.shopId.eq(shopId),
            menuEntity.isDeleted.isFalse()
        )
        .fetch();
  }

  public Optional<MenuEntity> findById(String menuId) {
    return Optional.ofNullable(queryFactory
        .select(menuEntity)
        .from(menuEntity)
        .where(
            menuEntity.menuId.eq(menuId),
            menuEntity.isDeleted.isFalse()
        )
        .fetchFirst()
    );
  }

  public List<MenuEntity> findAllByIds(List<String> menuId) {
    return queryFactory
        .select(menuEntity)
        .from(menuEntity)
        .where(
            menuEntity.menuId.in(menuId)
        )
        .fetch();
  }

  public String findCategoryIdBy(String menuId) {
    return queryFactory
        .select(categoryEntity.categoryId)
        .from(menuEntity)
        .innerJoin(categoryMenuEntity)
        .on(menuEntity.menuId.eq(categoryMenuEntity.menuId))
        .innerJoin(categoryEntity)
        .on(categoryMenuEntity.categoryId.eq(categoryEntity.categoryId))
        .where(
            menuEntity.menuId.eq(menuId)
        )
        .fetchFirst();
  }

  public List<MenuEntity> findAllByCategoryId(String categoryId) {
    return queryFactory
        .select(menuEntity)
        .from(menuEntity)
        .innerJoin(categoryMenuEntity)
        .on(menuEntity.menuId.eq(categoryMenuEntity.menuId))
        .where(
            categoryMenuEntity.categoryId.eq(categoryId)
        )
        .fetch();
  }


  public List<MenuEntity> findAllByCategoryIds(List<String> categoryId) {
    return queryFactory
        .select(menuEntity)
        .from(menuEntity)
        .innerJoin(categoryMenuEntity)
        .on(menuEntity.menuId.eq(categoryMenuEntity.menuId))
        .where(
            categoryMenuEntity.categoryId.in(categoryId)
        )
        .fetch();
  }

}
