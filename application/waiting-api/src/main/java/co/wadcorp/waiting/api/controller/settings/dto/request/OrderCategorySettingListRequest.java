package co.wadcorp.waiting.api.controller.settings.dto.request;


import co.wadcorp.waiting.api.controller.support.NonDuplicateOrderingConstraint;
import co.wadcorp.waiting.api.controller.support.Ordering;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategorySettingsListServiceRequest.OrderCategoryServiceDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCategorySettingListRequest {

  @Valid
  @NonDuplicateOrderingConstraint
  private List<OrderCategoryDto> categories;

  @Builder
  private OrderCategorySettingListRequest(List<OrderCategoryDto> categories) {
    this.categories = categories;
  }

  public OrderCategorySettingsListServiceRequest toServiceRequest() {
    return OrderCategorySettingsListServiceRequest.builder()
        .categories(categories.stream()
            .map(item -> OrderCategoryServiceDto.builder()
                .id(item.id)
                .name(item.name)
                .ordering(item.ordering)
                .build()
            )
            .toList()
        )
        .build();
  }

  @Getter
  @NoArgsConstructor
  public static class OrderCategoryDto implements Ordering {

    @NotBlank(message = "카테고리 id는 필수입니다.")
    private String id;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name;

    @Positive(message = "카테고리 순서는 1 이상의 필수값입니다.")
    private int ordering;

    @Builder
    private OrderCategoryDto(String id, String name, int ordering) {
      this.id = id;
      this.name = name;
      this.ordering = ordering;
    }

  }

}
