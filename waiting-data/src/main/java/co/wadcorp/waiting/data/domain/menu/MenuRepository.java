package co.wadcorp.waiting.data.domain.menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

  MenuEntity save(MenuEntity menuEntity);

  Optional<MenuEntity> findByMenuId(String menuId);

  List<MenuEntity> findAllByMenuIdIn(List<String> menuIds);

  List<MenuEntity> findAll();

  void deleteAllInBatch();

}
