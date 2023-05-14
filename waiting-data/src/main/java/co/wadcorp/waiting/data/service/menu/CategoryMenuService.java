package co.wadcorp.waiting.data.service.menu;

import co.wadcorp.waiting.data.domain.menu.CategoryMenuEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryMenuService {

  private final CategoryMenuRepository categoryMenuRepository;

  public CategoryMenuEntity save(CategoryMenuEntity categoryMenu) {
    return categoryMenuRepository.save(categoryMenu);
  }

  public void removeMapping(String menuId) {
    categoryMenuRepository.deleteByMenuId(menuId);
  }

}
