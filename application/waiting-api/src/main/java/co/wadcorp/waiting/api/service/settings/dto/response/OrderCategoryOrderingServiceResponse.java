package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCategoryOrderingServiceResponse {

  private final List<MappingCategoryServiceDto> categories;

  @Builder
  private OrderCategoryOrderingServiceResponse(List<MappingCategoryServiceDto> categories) {
    this.categories = categories;
  }

  public static OrderCategoryOrderingServiceResponse of(
      List<DisplayCategoryEntity> displayCategories) {
    return OrderCategoryOrderingServiceResponse.builder()
        .categories(displayCategories.stream()
            .map(displayCategory -> MappingCategoryServiceDto.builder()
                .id(displayCategory.getCategoryId())
                .ordering(displayCategory.getOrdering())
                .build()
            )
            .sorted()
            .toList()
        )
        .build();
  }

  @Getter
  public static class MappingCategoryServiceDto implements Comparable<MappingCategoryServiceDto> {

    private final String id;
    private final int ordering;

    @Builder
    private MappingCategoryServiceDto(String id, int ordering) {
      this.id = id;
      this.ordering = ordering;
    }

    @Override
    public int compareTo(MappingCategoryServiceDto o) {
      return ordering - o.ordering;
    }

  }

}
