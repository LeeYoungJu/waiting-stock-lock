package co.wadcorp.waiting.api.service.waiting.management;

import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementOrderMenuResponse;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.service.stock.StockService;
import co.wadcorp.waiting.data.query.displaymenu.DisplayCategoryQuery;
import co.wadcorp.waiting.data.query.displaymenu.DisplayMenuQuery;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenusDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ManagementDisplayMenuApiService {

  private final DisplayMenuQuery displayMenuQuery;
  private final DisplayCategoryQuery displayCategoryQuery;

  private final StockService stockService;

  public ManagementOrderMenuResponse getDisplayMenu(String shopId, DisplayMappingType displayMappingType, LocalDate operationDate) {

    DisplayMenusDto displayMenuDto = new DisplayMenusDto(
        displayMenuQuery.getDisplayMenu(shopId, displayMappingType)
    );

    List<DisplayCategoryDto> displayCategory = displayCategoryQuery.getDisplayCategory(
        displayMenuDto.getCategoryIds(),
        displayMappingType
    );
    Map<String, List<DisplayMenuDto>> collectCategoryMenus = displayMenuDto.toMapByCategoryId();

    Map<String, StockEntity> collectStocks = stockService.getStocks(
            displayMenuDto.getMenuIds(),
            operationDate
        )
        .stream()
        .collect(Collectors.toMap(StockEntity::getMenuId, item -> item));

    List<ManagementOrderMenuResponse.CategoryDto> categories = convertCategory(
        displayCategory,
        collectCategoryMenus,
        collectStocks
    );

    return ManagementOrderMenuResponse.builder()
        .categories(categories)
        .build();
  }

  private List<ManagementOrderMenuResponse.CategoryDto> convertCategory(
      List<DisplayCategoryDto> displayCategory,
      Map<String, List<DisplayMenuDto>> collectCategoryMenus,
      Map<String, StockEntity> collectStocks
  ) {
    return displayCategory.stream()
        .sorted()
        .map(categoryDto -> {
          List<DisplayMenuDto> displayMenuDtos = collectCategoryMenus.get(
              categoryDto.getCategoryId());

          return ManagementOrderMenuResponse.CategoryDto.builder()
              .id(categoryDto.getCategoryId())
              .name(categoryDto.getCategoryName())
              .ordering(categoryDto.getOrdering())
              .menus(displayMenuDtos.stream()
                  .sorted()
                  .map(item -> ManagementOrderMenuResponse.MenuDto.of(item, collectStocks.get(item.getMenuId())))
                  .toList())
              .build();
        })
        .toList();
  }
}
