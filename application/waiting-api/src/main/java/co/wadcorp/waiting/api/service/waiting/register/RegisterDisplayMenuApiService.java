package co.wadcorp.waiting.api.service.waiting.register;

import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse.CategoryDto;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse.MenuDto;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.domain.stock.StockEntity;
import co.wadcorp.waiting.data.query.displaymenu.DisplayCategoryQuery;
import co.wadcorp.waiting.data.query.displaymenu.DisplayMenuQuery;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenusDto;
import co.wadcorp.waiting.data.service.stock.StockService;
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
public class RegisterDisplayMenuApiService {

  private final DisplayMenuQuery displayMenuQuery;
  private final DisplayCategoryQuery displayCategoryQuery;

  private final StockService stockService;


  public RegisterWaitingOrderMenuResponse getOrderMenu(String shopId,
      DisplayMappingType displayMappingType, LocalDate operationDate) {

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

    List<CategoryDto> categories = convertCategory(
        displayCategory,
        collectCategoryMenus,
        collectStocks
    );

    return RegisterWaitingOrderMenuResponse.builder()
        .categories(categories)
        .build();
  }

  private List<CategoryDto> convertCategory(
      List<DisplayCategoryDto> displayCategory,
      Map<String, List<DisplayMenuDto>> collectCategoryMenus,
      Map<String, StockEntity> collectStocks
  ) {
    return displayCategory.stream()
        .sorted()
        .map(categoryDto -> {
          List<DisplayMenuDto> displayMenuDtos = collectCategoryMenus.get(
              categoryDto.getCategoryId());

          return CategoryDto.builder()
              .id(categoryDto.getCategoryId())
              .name(categoryDto.getCategoryName())
              .ordering(categoryDto.getOrdering())
              .menus(displayMenuDtos.stream()
                  .sorted()
                  .map(item -> MenuDto.of(item, collectStocks.get(item.getMenuId())))
                  .toList())
              .build();
        })
        .toList();
  }
}
