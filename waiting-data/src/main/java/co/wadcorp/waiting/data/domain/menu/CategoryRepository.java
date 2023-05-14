package co.wadcorp.waiting.data.domain.menu;

import java.util.List;

public interface CategoryRepository {

  CategoryEntity save(CategoryEntity categoryEntity);

  List<CategoryEntity> findAllByShopIdAndIsDeletedIsFalse(String shopId);

  List<CategoryEntity> findAll();

}
