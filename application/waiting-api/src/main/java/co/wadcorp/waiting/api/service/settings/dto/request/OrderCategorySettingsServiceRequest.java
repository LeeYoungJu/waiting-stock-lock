package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCategorySettingsServiceRequest {

  private String id;
  private String name;

  @Builder
  public OrderCategorySettingsServiceRequest(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public CategoryEntity toEntity(String shopId, int ordering) {
    return CategoryEntity.builder()
        .categoryId(id)
        .shopId(shopId)
        .name(name)
        .ordering(ordering)
        .build();
  }

}
