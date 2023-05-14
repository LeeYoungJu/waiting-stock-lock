package co.wadcorp.waiting.api.controller.settings.dto.request;

import co.wadcorp.waiting.api.controller.support.NonDuplicateOrderingConstraint;
import co.wadcorp.waiting.api.controller.support.Ordering;
import co.wadcorp.waiting.api.service.settings.dto.request.MenuType;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryMappingSaveServiceRequest;
import co.wadcorp.waiting.api.service.settings.dto.request.OrderCategoryMappingSaveServiceRequest.MappingMenuServiceDto;
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
public class OrderCategoryMappingSaveRequest {

  @NotNull(message = "메뉴판 타입은 필수입니다.")
  private MenuType menuType;

  @NotNull(message = "카테고리 전체 체크 여부는 필수입니다.")
  private Boolean allChecked;

  @Valid
  @NotEmpty(message = "메뉴 리스트는 필수입니다.")
  @NonDuplicateOrderingConstraint
  private List<MappingMenuDto> menus;

  @Builder
  private OrderCategoryMappingSaveRequest(MenuType menuType, Boolean allChecked,
      List<MappingMenuDto> menus) {
    this.menuType = menuType;
    this.allChecked = allChecked;
    this.menus = menus;
  }

  public OrderCategoryMappingSaveServiceRequest toServiceRequest() {
    return OrderCategoryMappingSaveServiceRequest.builder()
        .displayMappingType(menuType.getDisplayMappingType())
        .allChecked(allChecked)
        .menus(menus.stream()
            .map(menu -> MappingMenuServiceDto.builder()
                .id(menu.id)
                .isChecked(menu.isChecked)
                .ordering(menu.ordering)
                .build()
            )
            .toList()
        )
        .build();
  }

  @Getter
  @NoArgsConstructor
  public static class MappingMenuDto implements Ordering {

    @NotBlank(message = "메뉴 ID는 필수입니다.")
    private String id;

    @NotNull(message = "메뉴 체크 여부는 필수입니다.")
    private Boolean isChecked;

    @Positive(message = "카테고리 순서는 1 이상의 필수값입니다.")
    private int ordering;

    @Builder
    private MappingMenuDto(String id, Boolean isChecked, int ordering) {
      this.id = id;
      this.isChecked = isChecked;
      this.ordering = ordering;
    }

  }

}
