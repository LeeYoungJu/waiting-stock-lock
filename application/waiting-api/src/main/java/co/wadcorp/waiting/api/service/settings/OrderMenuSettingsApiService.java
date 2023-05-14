package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.libs.stream.StreamUtils.convert;
import static co.wadcorp.libs.stream.StreamUtils.convertToMap;

import co.wadcorp.waiting.api.service.settings.dto.request.OrderMenuSettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsResponse;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.CategoryMenuEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.query.displaymenu.DisplayCategoryQueryRepository;
import co.wadcorp.waiting.data.query.displaymenu.DisplayMenuQueryRepository;
import co.wadcorp.waiting.data.query.menu.CategoryMenuQueryRepository;
import co.wadcorp.waiting.data.query.menu.CategoryQueryRepository;
import co.wadcorp.waiting.data.query.menu.MenuQueryRepository;
import co.wadcorp.waiting.data.service.displaymenu.DisplayMenuService;
import co.wadcorp.waiting.data.service.menu.CategoryMenuService;
import co.wadcorp.waiting.data.service.menu.MenuService;
import co.wadcorp.waiting.data.service.stock.StockService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderMenuSettingsApiService {

  private final CategoryQueryRepository categoryQueryRepository;
  private final MenuQueryRepository menuQueryRepository;
  private final CategoryMenuQueryRepository categoryMenuQueryRepository;
  private final MenuService menuService;
  private final CategoryMenuService categoryMenuService;
  private final DisplayCategoryQueryRepository displayCategoryQueryRepository;
  private final DisplayMenuService displayMenuService;
  private final DisplayMenuQueryRepository displayMenuQueryRepository;
  private final StockService stockService;

  @Transactional // 메뉴 등록 후 바로 리스트 조회하기 때문에 writer DB에서 조회한다.
  public OrderMenuSettingsListResponse getMenus(String shopId) {
    List<CategoryEntity> categories = categoryQueryRepository.findAllBy(shopId);
    Map<String, Set<String>> categoryMenuGroupingMap = createCategoryMenuMap(categories);
    Map<String, MenuEntity> menuMap = createMenuMap(shopId);

    return OrderMenuSettingsListResponse.of(categories, categoryMenuGroupingMap, menuMap);
  }

  public OrderMenuSettingsResponse getMenu(String menuId) {
    MenuEntity menu = menuQueryRepository.findById(menuId)
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MENU));
    String categoryId = menuQueryRepository.findCategoryIdBy(menuId);

    return OrderMenuSettingsResponse.of(menu, categoryId);
  }

  @Transactional
  public OrderMenuSettingsResponse create(String shopId, OrderMenuSettingsServiceRequest request,
      LocalDate operationDate) {
    String categoryId = request.getCategoryId();

    int nextOrdering = findNextOrdering(categoryId);
    MenuEntity newMenu = request.toEntity(shopId, nextOrdering);
    MenuEntity savedMenu = menuService.save(newMenu);

    categoryMenuService.save(CategoryMenuEntity.builder()
        .categoryId(categoryId)
        .menuId(savedMenu.getMenuId())
        .build()
    );

    saveDisplayMenus(categoryId, savedMenu);

    stockService.save(StockEntity.of(savedMenu, operationDate));

    return OrderMenuSettingsResponse.of(savedMenu, categoryId);
  }

  @Transactional
  public OrderMenuSettingsResponse update(String menuId, OrderMenuSettingsServiceRequest request,
      LocalDate operationDate) {
    String requestCategoryId = request.getCategoryId();

    MenuEntity menu = menuService.findById(menuId);
    updateMenu(menu, request);
    updateCategoryId(menu, requestCategoryId);

    List<DisplayMenuEntity> displayMenus = displayMenuService.findByMenuId(menuId);
    displayMenus.forEach(displayMenu ->
        displayMenu.update(requestCategoryId, request.getName(), request.getUnitPrice())
    );

    stockService.updateDailyStock(menu, operationDate);

    return OrderMenuSettingsResponse.of(menu, requestCategoryId);
  }

  @Transactional
  public void delete(String menuId) {
    categoryMenuService.removeMapping(menuId);

    MenuEntity menu = menuService.findById(menuId);
    menu.delete();

    displayMenuService.removeByMenuId(menuId);
  }

  private Map<String, Set<String>> createCategoryMenuMap(List<CategoryEntity> categories) {
    List<String> categoryIds = convert(categories, CategoryEntity::getCategoryId);
    List<CategoryMenuEntity> categoryMenus = categoryMenuQueryRepository.findAllBy(categoryIds);

    return categoryMenus.stream()
        .collect(Collectors.groupingBy(
            CategoryMenuEntity::getCategoryId,
            Collectors.mapping(CategoryMenuEntity::getMenuId, Collectors.toSet())
        ));
  }

  private Map<String, MenuEntity> createMenuMap(String shopId) {
    List<MenuEntity> menus = menuQueryRepository.findAllBy(shopId);
    return convertToMap(menus, MenuEntity::getMenuId);
  }

  private void saveDisplayMenus(String categoryId, MenuEntity savedMenu) {
    for (DisplayMappingType displayMappingType : DisplayMappingType.values()) {
      int nextOrderingOfDisplayMenu = findNextOrderingOfDisplayMenu(categoryId, displayMappingType);
      DisplayMenuEntity newDisplayMenu = DisplayMenuEntity.of(savedMenu, categoryId,
          displayMappingType, nextOrderingOfDisplayMenu);

      boolean allChecked = displayCategoryQueryRepository.findAllCheckedByCategoryId(categoryId,
          displayMappingType);
      if (allChecked) {
        newDisplayMenu.checked();
      }

      displayMenuService.save(newDisplayMenu);
    }
  }

  private int findNextOrderingOfDisplayMenu(String categoryId,
      DisplayMappingType displayMappingType) {
    List<DisplayMenuEntity> displayMenus = displayMenuQueryRepository.findAllBy(categoryId,
        displayMappingType);
    int lastOrdering = findLastOrderingOfDisplayMenus(displayMenus);
    return lastOrdering + 1;
  }

  private int findLastOrderingOfDisplayMenus(List<DisplayMenuEntity> menus) {
    return menus.stream()
        .max(Comparator.naturalOrder())
        .map(DisplayMenuEntity::getOrdering)
        .orElse(0);
  }

  private void updateMenu(MenuEntity menu, OrderMenuSettingsServiceRequest request) {
    menu.update(
        request.getName(), request.getUnitPrice(), request.isUsedDailyStock(),
        request.getDailyStock(), request.isUsedMenuQuantityPerTeam(),
        request.getMenuQuantityPerTeam()
    );
  }

  private void updateCategoryId(MenuEntity menu, String requestCategoryId) {
    String existingCategoryId = categoryMenuQueryRepository.getCategoryIdByMenuId(menu.getMenuId());
    if (requestCategoryId.equals(existingCategoryId)) {
      return;
    }

    int nextOrdering = findNextOrdering(requestCategoryId);
    menu.updateOrdering(nextOrdering);

    categoryMenuService.removeMapping(menu.getMenuId());
    categoryMenuService.save(CategoryMenuEntity.builder()
        .categoryId(requestCategoryId)
        .menuId(menu.getMenuId())
        .build()
    );
  }

  private int findNextOrdering(String categoryId) {
    List<MenuEntity> menus = menuQueryRepository.findAllByCategoryId(categoryId);
    int lastOrdering = findLastOrdering(menus);
    return lastOrdering + 1;
  }

  private int findLastOrdering(List<MenuEntity> categories) {
    return categories.stream()
        .max(Comparator.naturalOrder())
        .map(MenuEntity::getOrdering)
        .orElse(0);
  }

}
