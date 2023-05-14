package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCategorySettingsListServiceRequest {

  private List<OrderCategoryServiceDto> categories;

  @Builder
  private OrderCategorySettingsListServiceRequest(List<OrderCategoryServiceDto> categories) {
    this.categories = categories;
  }

  @Getter
  @NoArgsConstructor
  public static class OrderCategoryServiceDto {

    private String id;
    private String name;
    private int ordering;

    @Builder
    private OrderCategoryServiceDto(String id, String name, int ordering) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
    }

    public CategoryEntity toEntity(String shopId) {
      return CategoryEntity.builder()
          .categoryId(id)
          .shopId(shopId)
          .name(name)
          .ordering(ordering)
          .build();
    }

  }

}
