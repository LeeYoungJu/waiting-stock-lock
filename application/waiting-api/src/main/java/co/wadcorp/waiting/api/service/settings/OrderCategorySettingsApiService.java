package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest.OrderCategoryServiceDto;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsResponse;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import co.wadcorp.waiting.data.domain.menu.MenuEntity;
import co.wadcorp.waiting.data.query.displaymenu.DisplayCategoryQueryRepository;
import co.wadcorp.waiting.data.query.menu.CategoryMenuQueryRepository;
import co.wadcorp.waiting.data.query.menu.CategoryQueryRepository;
import co.wadcorp.waiting.data.service.displaymenu.DisplayCategoryService;
import co.wadcorp.waiting.data.service.displaymenu.DisplayMenuService;
import co.wadcorp.waiting.data.service.menu.CategoryService;
import co.wadcorp.waiting.data.service.menu.MenuService;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderCategorySettingsApiService {

  private final CategoryQueryRepository categoryQueryRepository;
  private final CategoryMenuQueryRepository categoryMenuQueryRepository;
  private final CategoryService categoryService;
  private final MenuService menuService;
  private final DisplayCategoryService displayCategoryService;
  private final DisplayCategoryQueryRepository displayCategoryQueryRepository;
  private final DisplayMenuService displayMenuService;

  @Transactional
  public OrderCategorySettingsResponse saveCategory(String shopId,
      OrderCategorySettingsServiceRequest request) {

    int nextOrdering = getNextOrdering(shopId);

    CategoryEntity newCategory = request.toEntity(shopId, nextOrdering);
    CategoryEntity savedCategory = categoryService.save(newCategory);

    saveDisplayCategories(shopId, savedCategory);

    return OrderCategorySettingsResponse.of(savedCategory);
  }

  @Transactional // 카테고리 등록 후 바로 리스트 조회하기 때문에 writer DB에서 조회한다.
  public OrderCategorySettingsListResponse getCategories(String shopId) {
    List<CategoryEntity> categories = categoryQueryRepository.findAllBy(shopId);

    return OrderCategorySettingsListResponse.of(categories);
  }

  @Transactional
  public OrderCategorySettingsListResponse saveAllCategories(String shopId,
      OrderCategorySettingsListServiceRequest request
  ) {
    List<OrderCategoryServiceDto> requestCategories = request.getCategories();
    Map<String, OrderCategoryServiceDto> requestCategoryMap = requestCategories.stream()
        .collect(Collectors.toMap(OrderCategoryServiceDto::getId, dto -> dto));

    List<CategoryEntity> existingCategories = categoryService.findAllBy(shopId);
    Map<String, CategoryEntity> existingCategoryMap = existingCategories.stream()
        .collect(Collectors.toMap(CategoryEntity::getCategoryId, dto -> dto));

    createAndUpdate(shopId, requestCategories, existingCategoryMap);
    delete(requestCategoryMap, existingCategories);

    List<CategoryEntity> categories = categoryQueryRepository.findAllBy(shopId);
    return OrderCategorySettingsListResponse.of(categories);
  }

  private void saveDisplayCategories(String shopId, CategoryEntity savedCategory) {
    for (DisplayMappingType displayMappingType : DisplayMappingType.values()) {
      int nextOrderingOfDisplayCategory = findNextOrderingOfDisplayCategory(shopId,
          displayMappingType);

      displayCategoryService.save(
          DisplayCategoryEntity.of(savedCategory, displayMappingType, nextOrderingOfDisplayCategory)
      );
    }
  }

  private int findNextOrderingOfDisplayCategory(String shopId,
      DisplayMappingType displayMappingType) {
    List<DisplayCategoryEntity> displayCategories = displayCategoryQueryRepository.findAllBy(shopId,
        displayMappingType);
    int lastOrdering = findLastOrderingOfDisplayCategories(displayCategories);
    return lastOrdering + 1;
  }

  private int findLastOrderingOfDisplayCategories(List<DisplayCategoryEntity> categories) {
    return categories.stream()
        .max(Comparator.naturalOrder())
        .map(DisplayCategoryEntity::getOrdering)
        .orElse(0);
  }

  private int getNextOrdering(String shopId) {
    List<CategoryEntity> categories = categoryQueryRepository.findAllBy(shopId);
    int lastOrdering = findLastOrdering(categories);
    return lastOrdering + 1;
  }

  private int findLastOrdering(List<CategoryEntity> categories) {
    return categories.stream()
        .max(Comparator.naturalOrder())
        .map(CategoryEntity::getOrdering)
        .orElse(0);
  }

  private void createAndUpdate(String shopId, List<OrderCategoryServiceDto> requestCategories,
      Map<String, CategoryEntity> existingCategoryMap) {
    for (OrderCategoryServiceDto requestCategory : requestCategories) {
      Optional.ofNullable(existingCategoryMap.get(requestCategory.getId()))
          .ifPresentOrElse(
              existingCategory -> update(existingCategory, requestCategory),
              () -> save(shopId, requestCategory)
          );
    }
  }

  private void update(CategoryEntity existingCategory, OrderCategoryServiceDto requestCategory) {
    existingCategory.update(requestCategory.getName(), requestCategory.getOrdering());
  }

  private void save(String shopId, OrderCategoryServiceDto requestCategory) {
    CategoryEntity newCategory = requestCategory.toEntity(shopId);
    CategoryEntity savedCategory = categoryService.save(newCategory);

    saveDisplayCategories(shopId, savedCategory);
  }

  private void delete(Map<String, OrderCategoryServiceDto> requestCategoryMap,
      List<CategoryEntity> existingCategories) {
    List<CategoryEntity> deletedCategories = existingCategories.stream()
        .filter(existingCategory -> doesNotExist(requestCategoryMap, existingCategory))
        .toList();

    deletedCategories.forEach(CategoryEntity::delete);
    deleteMenusBy(deletedCategories);

    displayCategoryService.removeAllByCategoryIds(
        convert(deletedCategories, CategoryEntity::getCategoryId)
    );
  }

  private boolean doesNotExist(Map<String, OrderCategoryServiceDto> requestCategoryMap,
      CategoryEntity existingCategory) {
    OrderCategoryServiceDto serviceDto = requestCategoryMap.get(existingCategory.getCategoryId());
    return serviceDto == null;
  }

  private void deleteMenusBy(List<CategoryEntity> deletedCategories) {
    List<String> deletedCategoryIds = convert(deletedCategories, CategoryEntity::getCategoryId);

    List<String> deletedMenuIds = categoryMenuQueryRepository.findAllMenuIdsBy(deletedCategoryIds);
    menuService.findAllByMenuIdIn(deletedMenuIds)
        .forEach(MenuEntity::delete);

    displayMenuService.removeAllByMenuId(deletedMenuIds);
  }

}
