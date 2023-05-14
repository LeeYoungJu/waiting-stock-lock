package co.wadcorp.waiting.api.service.settings;

import static co.wadcorp.libs.stream.StreamUtils.groupingBySet;

import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryMappingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryOrderingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderDisplayMenuServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategoryOrderingServiceResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderDisplayMenuMappingResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuMappingServiceResponse;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import co.wadcorp.waiting.data.query.displaymenu.DisplayCategoryQueryRepository;
import co.wadcorp.waiting.data.query.displaymenu.DisplayMenuQueryRepository;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuNamePriceDto;
import co.wadcorp.waiting.data.service.displaymenu.DisplayCategoryService;
import co.wadcorp.waiting.data.service.displaymenu.DisplayMenuService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderMenuMappingApiService {

  private final DisplayCategoryQueryRepository displayCategoryQueryRepository;
  private final DisplayMenuQueryRepository displayMenuQueryRepository;
  private final DisplayCategoryService displayCategoryService;
  private final DisplayMenuService displayMenuService;

  public OrderDisplayMenuMappingResponse getDisplayMappingMenus(String shopId,
      OrderDisplayMenuServiceRequest request) {
    DisplayMappingType displayMappingType = request.getDisplayMappingType();

    List<DisplayCategoryDto> displayCategories = displayCategoryQueryRepository.findDisplayCategoriesBy(
        shopId, displayMappingType);

    List<DisplayMenuNamePriceDto> displayMenus = displayMenuQueryRepository.findDisplayMenusBy(
        shopId, displayMappingType);
    Map<String, Set<DisplayMenuNamePriceDto>> displayMenuMap = groupingBySet(displayMenus,
        DisplayMenuNamePriceDto::getCategoryId);

    return OrderDisplayMenuMappingResponse.of(displayCategories, displayMenuMap);
  }

  @Transactional
  public OrderMenuMappingServiceResponse saveMenuMapping(String categoryId,
      OrderCategoryMappingSaveServiceRequest request
  ) {
    DisplayMappingType displayMappingType = request.getDisplayMappingType();

    DisplayCategoryEntity displayCategory = displayCategoryService.findByCategoryIdWithType(
        categoryId, displayMappingType);
    if (request.isAllChecked()) {
      displayCategory.allChecked();
    } else {
      displayCategory.releaseAllChecked();
    }

    List<DisplayMenuEntity> displayMenus = displayMenuService.findAllByCategoryIdAndMappingType(
        categoryId, displayMappingType);
    Map<String, DisplayMenuEntity> displayMenuMap = displayMenus.stream()
        .collect(Collectors.toMap(DisplayMenuEntity::getMenuId, entity -> entity));

    request.getMenus().forEach(menuDto -> {
      DisplayMenuEntity displayMenu = displayMenuMap.get(menuDto.getId());
      displayMenu.update(menuDto.isChecked(), menuDto.getOrdering());
    });

    return OrderMenuMappingServiceResponse.of(displayCategory, displayMenus);
  }

  @Transactional
  public OrderCategoryOrderingServiceResponse saveCategoryOrdering(String shopId,
      OrderCategoryOrderingSaveServiceRequest request
  ) {
    DisplayMappingType displayMappingType = request.getDisplayMappingType();

    List<DisplayCategoryEntity> displayCategories = displayCategoryService.findByShopIdWithType(
        shopId, displayMappingType);
    Map<String, DisplayCategoryEntity> displayCategoryMap = displayCategories.stream()
        .collect(Collectors.toMap(DisplayCategoryEntity::getCategoryId, entity -> entity));

    request.getCategories()
        .forEach(categoryDto -> {
          DisplayCategoryEntity displayCategory = displayCategoryMap.get(categoryDto.getId());
          displayCategory.update(categoryDto.getOrdering());
        });

    return OrderCategoryOrderingServiceResponse.of(displayCategories);
  }

}
