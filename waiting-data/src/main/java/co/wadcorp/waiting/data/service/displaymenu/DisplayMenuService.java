package co.wadcorp.waiting.data.service.displaymenu;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class DisplayMenuService {

  private final DisplayMenuRepository displayMenuRepository;

  public DisplayMenuEntity save(DisplayMenuEntity displayMenuEntity) {
    return displayMenuRepository.save(displayMenuEntity);
  }

  public List<DisplayMenuEntity> findByMenuId(String menuId) {
    return displayMenuRepository.findByMenuId(menuId);
  }

  public List<DisplayMenuEntity> findAllByCategoryIdAndMappingType(String categoryId,
      DisplayMappingType displayMappingType) {
    return displayMenuRepository.findAllByCategoryIdAndDisplayMappingType(categoryId,
        displayMappingType);
  }

  public void removeByMenuId(String menuId) {
    displayMenuRepository.deleteByMenuId(menuId);
  }

  public void removeAllByMenuId(List<String> menuIds) {
    displayMenuRepository.deleteAllByMenuIdIn(menuIds);
  }

}
