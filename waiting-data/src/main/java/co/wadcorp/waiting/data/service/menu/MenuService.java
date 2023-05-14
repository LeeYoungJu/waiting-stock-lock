package co.wadcorp.waiting.data.service.menu;

import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuRepository;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MenuService {

  private final MenuRepository menuRepository;

  public MenuEntity save(MenuEntity menu) {
    return menuRepository.save(menu);
  }

  public MenuEntity findById(String menuId) {
    return menuRepository.findByMenuId(menuId)
        .orElseThrow(() -> AppException.ofBadRequest(ErrorCode.NOT_FOUND_MENU));
  }

  public List<MenuEntity> findAllByMenuIdIn(List<String> menuIds) {
    return menuRepository.findAllByMenuIdIn(menuIds);
  }

  public Map<String, MenuEntity> getMenuIdMenuEntityMap(List<String> menuIds) {
    List<MenuEntity> menuEntities = menuRepository.findAllByMenuIdIn(menuIds);
    return menuEntities.stream()
        .collect(Collectors.toMap(MenuEntity::getMenuId, item -> item));
  }
}
