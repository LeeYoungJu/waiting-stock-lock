package co.wadcorp.waiting.data.infra.menu;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCategoryRepository extends CategoryRepository,
    JpaRepository<CategoryEntity, Long> {

  List<CategoryEntity> findAllByShopIdAndIsDeletedIsFalse(String shopId);

}
