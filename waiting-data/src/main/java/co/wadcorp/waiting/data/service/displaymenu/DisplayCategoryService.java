package co.wadcorp.waiting.data.service.displaymenu;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryRepository;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DisplayCategoryService {

  private final DisplayCategoryRepository displayCategoryRepository;

  public DisplayCategoryEntity save(DisplayCategoryEntity displayCategoryEntity) {
    return displayCategoryRepository.save(displayCategoryEntity);
  }

  public DisplayCategoryEntity findByCategoryIdWithType(String categoryId,
      DisplayMappingType displayMappingType) {
    return displayCategoryRepository.findByCategoryIdAndDisplayMappingType(categoryId,
        displayMappingType);
  }

  public List<DisplayCategoryEntity> findByShopIdWithType(String shopId,
      DisplayMappingType displayMappingType) {
    return displayCategoryRepository.findAllByShopIdAndDisplayMappingType(shopId,
        displayMappingType);
  }

  public void removeAllByCategoryIds(List<String> categoryIds) {
    displayCategoryRepository.deleteAllByCategoryIdIn(categoryIds);
  }

}
