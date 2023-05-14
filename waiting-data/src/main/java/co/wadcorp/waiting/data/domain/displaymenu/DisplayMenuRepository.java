package co.wadcorp.waiting.data.domain.displaymenu;

import java.util.List;

public interface DisplayMenuRepository {

  DisplayMenuEntity save(DisplayMenuEntity displayMenuEntity);

  List<DisplayMenuEntity> findAll();

  List<DisplayMenuEntity> findByMenuId(String menuId);

  List<DisplayMenuEntity> findAllByCategoryIdAndDisplayMappingType(String categoryId, DisplayMappingType displayMappingType);

  void deleteByMenuId(String menuId);

  void deleteAllByMenuIdIn(List<String> menuIds);

}
