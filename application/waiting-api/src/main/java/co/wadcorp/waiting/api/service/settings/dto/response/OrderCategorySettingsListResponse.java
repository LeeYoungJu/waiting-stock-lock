package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderCategorySettingsListResponse(List<OrderCategoryDto> categories) {

  public static OrderCategorySettingsListResponse of(List<CategoryEntity> categories) {
    return OrderCategorySettingsListResponse.builder()
        .categories(categories.stream()
            .sorted()
            .map(OrderCategoryDto::of)
            .toList()
        )
        .build();
  }

  @Builder
  public record OrderCategoryDto(String id, String name, int ordering) {

    public static OrderCategoryDto of(CategoryEntity category) {
      return OrderCategoryDto.builder()
          .id(category.getCategoryId())
          .name(category.getName())
          .ordering(category.getOrdering())
          .build();
    }

  }

}
