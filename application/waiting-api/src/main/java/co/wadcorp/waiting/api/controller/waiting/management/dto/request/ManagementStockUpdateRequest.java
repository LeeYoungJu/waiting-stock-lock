package co.wadcorp.waiting.api.controller.waiting.management.dto.request;

import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest.ManagementStockCategoryServiceDto;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UpdateStockServiceRequest.ManagementStockMenuServiceDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ManagementStockUpdateRequest {

  @Valid
  @NotEmpty(message = "카테고리 리스트는 필수입니다.")
  private List<ManagementStockCategoryDto> categories;

  @Builder
  private ManagementStockUpdateRequest(List<ManagementStockCategoryDto> categories) {
    this.categories = categories;
  }

  @Getter
  @NoArgsConstructor
  public static class ManagementStockCategoryDto {

    @NotBlank(message = "카테고리 ID는 필수입니다.")
    private String id;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name;

    @Valid
    @NotEmpty(message = "메뉴 리스트는 필수입니다.")
    private List<ManagementStockMenuDto> menus;

    @Builder
    private ManagementStockCategoryDto(String id, String name, List<ManagementStockMenuDto> menus) {
      this.id = id;
      this.name = name;
      this.menus = menus;
    }

  }

  @Getter
  @NoArgsConstructor
  public static class ManagementStockMenuDto {

    @NotBlank(message = "메뉴 ID는 필수입니다.")
    private String id;

    @NotBlank(message = "메뉴 이름은 필수입니다.")
    private String name;

    @NotNull(message = "재고 추가수량은 필수입니다.")
    private Integer additionalQuantity;

    @NotNull(message = "재고 품절 여부는 필수입니다.")
    private Boolean isOutOfStock;

    @Builder
    private ManagementStockMenuDto(String id, String name, Integer additionalQuantity,
        Boolean isOutOfStock) {
      this.id = id;
      this.name = name;
      this.additionalQuantity = additionalQuantity;
      this.isOutOfStock = isOutOfStock;
    }

  }

  public UpdateStockServiceRequest toServiceRequest() {
    return UpdateStockServiceRequest.builder()
        .categories(this.categories.stream()
            .map(category -> ManagementStockCategoryServiceDto.builder()
                .id(category.id)
                .name(category.name)
                .menus(category.menus.stream()
                    .map(menu -> ManagementStockMenuServiceDto.builder()
                        .id(menu.id)
                        .name(menu.name)
                        .additionalQuantity(menu.additionalQuantity)
                        .isOutOfStock(menu.isOutOfStock)
                        .build())
                    .toList()
                )
                .build()
            )
            .toList()
        )
        .build();
  }

}
