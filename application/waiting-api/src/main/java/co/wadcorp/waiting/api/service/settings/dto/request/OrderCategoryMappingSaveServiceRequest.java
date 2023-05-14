package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCategoryMappingSaveServiceRequest {

  private final DisplayMappingType displayMappingType;
  private final boolean allChecked;
  private final List<MappingMenuServiceDto> menus;

  @Builder
  private OrderCategoryMappingSaveServiceRequest(DisplayMappingType displayMappingType,
      boolean allChecked, List<MappingMenuServiceDto> menus) {
    this.displayMappingType = displayMappingType;
    this.allChecked = allChecked;
    this.menus = menus;
  }

  @Getter
  public static class MappingMenuServiceDto {

    private final String id;
    private final boolean isChecked;
    private final int ordering;

    @Builder
    private MappingMenuServiceDto(String id, boolean isChecked, int ordering) {
      this.id = id;
      this.isChecked = isChecked;
      this.ordering = ordering;
    }

  }

}
