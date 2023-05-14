package co.wadcorp.waiting.api.controller.settings.dto.request;

import co.wadcorp.waiting.api.controller.support.NonDuplicateOrderingConstraint;
import co.wadcorp.waiting.api.controller.support.Ordering;
import co.wadcorp.waiting.api.service.settings.dto.request.MenuType;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryOrderingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryOrderingSaveServiceRequest.MappingCategoryServiceDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCategoryOrderingSaveRequest {

  @NotNull(message = "메뉴판 타입은 필수입니다.")
  private MenuType menuType;

  @Valid
  @NotEmpty(message = "카테고리 리스트는 필수입니다.")
  @NonDuplicateOrderingConstraint
  private List<MappingCategoryDto> categories;

  @Builder
  private OrderCategoryOrderingSaveRequest(MenuType menuType, List<MappingCategoryDto> categories) {
    this.menuType = menuType;
    this.categories = categories;
  }

  public OrderCategoryOrderingSaveServiceRequest toServiceRequest() {
    return OrderCategoryOrderingSaveServiceRequest.builder()
        .displayMappingType(menuType.getDisplayMappingType())
        .categories(categories.stream()
            .map(menu -> MappingCategoryServiceDto.builder()
                .id(menu.id)
                .ordering(menu.ordering)
                .build()
            )
            .toList()
        )
        .build();
  }

  @Getter
  @NoArgsConstructor
  public static class MappingCategoryDto implements Ordering {

    @NotBlank(message = "카테고리 ID는 필수입니다.")
    private String id;

    @Positive(message = "카테고리 순서는 1 이상의 필수값입니다.")
    private int ordering;

    @Builder
    private MappingCategoryDto(String id, int ordering) {
      this.id = id;
      this.ordering = ordering;
    }

  }

}
