package co.wadcorp.waiting.data.domain.displaymenu;

import java.util.List;

public interface DisplayCategoryRepository {

  DisplayCategoryEntity save(DisplayCategoryEntity displayCategoryEntity);

  List<DisplayCategoryEntity> findAll();

  DisplayCategoryEntity findByCategoryIdAndDisplayMappingType(String categoryId,
      DisplayMappingType displayMappingType);

  List<DisplayCategoryEntity> findAllByShopIdAndDisplayMappingType(String shopId,
      DisplayMappingType displayMappingType);

  void deleteAllByCategoryIdIn(List<String> categoryIds);

}
