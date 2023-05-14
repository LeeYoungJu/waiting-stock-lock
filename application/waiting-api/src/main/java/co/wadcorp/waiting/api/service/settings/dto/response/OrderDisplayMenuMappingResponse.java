package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayCategoryDto;
import co.wadcorp.waiting.data.query.displaymenu.dto.DisplayMenuNamePriceDto;
import co.wadcorp.waiting.data.support.Price;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderDisplayMenuMappingResponse {

  private final List<OrderDisplayCategoryDto> categories;

  @Builder
  private OrderDisplayMenuMappingResponse(List<OrderDisplayCategoryDto> categories) {
    this.categories = categories;
  }

  public static OrderDisplayMenuMappingResponse of(List<DisplayCategoryDto> displayCategories,
      Map<String, Set<DisplayMenuNamePriceDto>> displayMenuMap) {
    return OrderDisplayMenuMappingResponse.builder()
        .categories(displayCategories.stream()
            .sorted()
            .map(categoryDto -> createDisplayCategoryDto(categoryDto, displayMenuMap))
            .toList()
        )
        .build();
  }

  private static OrderDisplayCategoryDto createDisplayCategoryDto(
      DisplayCategoryDto categoryDto, Map<String, Set<DisplayMenuNamePriceDto>> displayMenuMap) {
    Set<DisplayMenuNamePriceDto> displayMenuNamePriceDtos = displayMenuMap.getOrDefault(
        categoryDto.getCategoryId(), Set.of());

    return OrderDisplayCategoryDto.of(categoryDto, displayMenuNamePriceDtos);
  }

  @Getter
  public static class OrderDisplayCategoryDto {

    private final String id;
    private final String name;
    private final int ordering;
    private final Boolean allChecked;
    private final List<OrderDisplayMenuDto> menus;

    @Builder
    private OrderDisplayCategoryDto(String id, String name, int ordering,
        Boolean allChecked, List<OrderDisplayMenuDto> menus) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.allChecked = allChecked;
      this.menus = menus;
    }

    public static OrderDisplayCategoryDto of(DisplayCategoryDto displayCategoryDto,
        Set<DisplayMenuNamePriceDto> displayMenuNamePriceDtos) {
      return OrderDisplayCategoryDto.builder()
          .id(displayCategoryDto.getCategoryId())
          .name(displayCategoryDto.getCategoryName())
          .ordering(displayCategoryDto.getOrdering())
          .allChecked(displayCategoryDto.isAllChecked())
          .menus(displayMenuNamePriceDtos.stream()
              .sorted()
              .map(OrderDisplayMenuDto::of)
              .toList()
          )
          .build();
    }

  }

  @Getter
  public static class OrderDisplayMenuDto {

    private final String id;
    private final String name;
    private final int ordering;
    private final BigDecimal unitPrice;
    private final Boolean isChecked;

    @Builder
    private OrderDisplayMenuDto(String id, String name, int ordering, Price unitPrice,
        Boolean isChecked) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
      this.unitPrice = unitPrice.value();
      this.isChecked = isChecked;
    }

    public static OrderDisplayMenuDto of(DisplayMenuNamePriceDto displayMenuNamePriceDto) {
      return OrderDisplayMenuDto.builder()
          .id(displayMenuNamePriceDto.getMenuId())
          .name(displayMenuNamePriceDto.getName())
          .ordering(displayMenuNamePriceDto.getMenuOrdering())
          .unitPrice(displayMenuNamePriceDto.getUnitPrice())
          .isChecked(displayMenuNamePriceDto.isChecked())
          .build();
    }

  }

}
