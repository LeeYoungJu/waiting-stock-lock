package co.wadcorp.waiting.data.query.menu;

import static co.wadcorp.waiting.data.domain.menu.QCategoryEntity.categoryEntity;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class CategoryQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<CategoryEntity> findAllBy(String shopId) {
    return queryFactory
        .select(categoryEntity)
        .from(categoryEntity)
        .where(
            categoryEntity.shopId.eq(shopId),
            categoryEntity.isDeleted.isFalse()
        )
        .fetch();
  }

}
