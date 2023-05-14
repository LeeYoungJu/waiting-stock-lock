package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCategoryOrderingSaveServiceRequest {

  private final DisplayMappingType displayMappingType;
  private final List<MappingCategoryServiceDto> categories;

  @Builder
  private OrderCategoryOrderingSaveServiceRequest(DisplayMappingType displayMappingType,
      List<MappingCategoryServiceDto> categories) {
    this.displayMappingType = displayMappingType;
    this.categories = categories;
  }

  @Getter
  public static class MappingCategoryServiceDto {

    private final String id;
    private final int ordering;

    @Builder
    private MappingCategoryServiceDto(String id, int ordering) {
      this.id = id;
      this.ordering = ordering;
    }

  }

}
