package co.wadcorp.waiting.data.infra.menu;

import co.wadcorp.waiting.data.domain.menu.CategoryMenuEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCategoryMenuRepository extends CategoryMenuRepository,
    JpaRepository<CategoryMenuEntity, Long> {

  void deleteByMenuId(String menuId);

}
