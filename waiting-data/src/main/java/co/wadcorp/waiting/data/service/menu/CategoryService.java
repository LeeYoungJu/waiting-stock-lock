package co.wadcorp.waiting.data.service.menu;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryEntity save(CategoryEntity entity) {
    return categoryRepository.save(entity);
  }

  public List<CategoryEntity> findAllBy(String shopId) {
    return categoryRepository.findAllByShopIdAndIsDeletedIsFalse(shopId);
  }

}
