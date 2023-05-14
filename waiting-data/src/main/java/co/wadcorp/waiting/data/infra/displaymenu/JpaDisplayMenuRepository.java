package co.wadcorp.waiting.data.infra.displaymenu;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDisplayMenuRepository extends DisplayMenuRepository,
    JpaRepository<DisplayMenuEntity, Long> {

  List<DisplayMenuEntity> findAllByCategoryIdAndDisplayMappingType(String categoryId, DisplayMappingType displayMappingType);

  void deleteByMenuId(String menuId);

  void deleteAllByMenuIdIn(List<String> menuIds);

}
