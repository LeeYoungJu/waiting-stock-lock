package co.wadcorp.waiting.data.infra.menu;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMenuRepository extends MenuRepository, JpaRepository<MenuEntity, Long> {

  List<MenuEntity> findAllByMenuIdIn(List<String> menuIds);

  Optional<MenuEntity> findByMenuId(String menuId);

}
