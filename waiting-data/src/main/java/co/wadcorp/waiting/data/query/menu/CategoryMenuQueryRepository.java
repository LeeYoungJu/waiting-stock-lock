package co.wadcorp.waiting.data.query.menu;

import static co.wadcorp.waiting.data.domain.menu.QCategoryMenuEntity.categoryMenuEntity;

import co.wadcorp.waiting.data.domain.menu.CategoryMenuEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class CategoryMenuQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<String> findAllMenuIdsBy(List<String> categoryIds) {
    return queryFactory
        .select(categoryMenuEntity.menuId)
        .from(categoryMenuEntity)
        .where(
            categoryMenuEntity.categoryId.in(categoryIds)
        )
        .fetch();
  }

  public List<CategoryMenuEntity> findAllBy(List<String> categoryIds) {
    return queryFactory
        .select(categoryMenuEntity)
        .from(categoryMenuEntity)
        .where(
            categoryMenuEntity.categoryId.in(categoryIds)
        )
        .fetch();
  }

  public String getCategoryIdByMenuId(String menuId) {
    return queryFactory
        .select(categoryMenuEntity.categoryId)
        .from(categoryMenuEntity)
        .where(
            categoryMenuEntity.menuId.eq(menuId)
        )
        .fetchFirst();
  }

}
