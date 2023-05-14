package co.wadcorp.waiting.data.domain.menu;

import java.util.List;

public interface CategoryMenuRepository {

  CategoryMenuEntity save(CategoryMenuEntity categoryMenuEntity);

  List<CategoryMenuEntity> findAll();

  void deleteByMenuId(String menuId);

}
