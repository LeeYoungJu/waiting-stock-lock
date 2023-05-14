package co.wadcorp.waiting.data.infra.displaymenu;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryRepository;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDisplayCategoryRepository extends DisplayCategoryRepository,
    JpaRepository<DisplayCategoryEntity, Long> {

  DisplayCategoryEntity findByCategoryIdAndDisplayMappingType(String categoryId,
      DisplayMappingType displayMappingType);

  List<DisplayCategoryEntity> findAllByShopIdAndDisplayMappingType(String shopId,
      DisplayMappingType displayMappingType);

  void deleteAllByCategoryIdIn(List<String> categoryIds);

}
