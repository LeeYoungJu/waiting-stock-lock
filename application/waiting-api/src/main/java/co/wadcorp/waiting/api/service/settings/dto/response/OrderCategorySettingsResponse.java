package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.menu.CategoryEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCategorySettingsResponse {

  private final String id;
  private final String name;
  private final int ordering;

  @Builder
  private OrderCategorySettingsResponse(String id, String name, int ordering) {
    this.id = id;
    this.name = name;
    this.ordering = ordering;
  }

  public static OrderCategorySettingsResponse of(CategoryEntity categoryEntity) {
    return OrderCategorySettingsResponse.builder()
        .id(categoryEntity.getCategoryId())
        .name(categoryEntity.getName())
        .ordering(categoryEntity.getOrdering())
        .build();
  }

}
