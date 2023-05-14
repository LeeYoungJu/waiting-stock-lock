package co.wadcorp.waiting.api.service.waiting.management.dto.request;

import co.wadcorp.waiting.data.service.stock.dto.StockMenuDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateStockServiceRequest {

  private final List<ManagementStockCategoryServiceDto> categories;

  @Builder
  private UpdateStockServiceRequest(List<ManagementStockCategoryServiceDto> categories) {
    this.categories = categories;
  }

  public List<StockMenuDto> getMenuDtos() {
    return categories.stream()
        .flatMap(categoryDto -> categoryDto.menus.stream())
        .map(menuDto -> StockMenuDto.builder()
            .id(menuDto.id)
            .name(menuDto.name)
            .additionalQuantity(menuDto.additionalQuantity)
            .isOutOfStock(menuDto.isOutOfStock)
            .build()
        )
        .toList();
  }

  @Getter
  public static class ManagementStockCategoryServiceDto {

    private final String id;
    private final String name;
    private final List<ManagementStockMenuServiceDto> menus;

    @Builder
    private ManagementStockCategoryServiceDto(String id, String name,
        List<ManagementStockMenuServiceDto> menus) {
      this.id = id;
      this.name = name;
      this.menus = menus;
    }

  }

  @Getter
  public static class ManagementStockMenuServiceDto {

    private final String id;
    private final String name;
    private final int additionalQuantity;
    private final boolean isOutOfStock;

    @Builder
    private ManagementStockMenuServiceDto(String id, String name, int additionalQuantity,
        boolean isOutOfStock) {
      this.id = id;
      this.name = name;
      this.additionalQuantity = additionalQuantity;
      this.isOutOfStock = isOutOfStock;
    }

  }

}
