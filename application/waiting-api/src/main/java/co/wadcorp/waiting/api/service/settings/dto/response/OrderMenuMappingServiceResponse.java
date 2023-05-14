package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayCategoryEntity;
import co.wadcorp.waiting.data.domain.displaymenu.DisplayMenuEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderMenuMappingServiceResponse {

  private final String id;
  private final Boolean allChecked;
  private final List<MappingMenu> menus;

  @Builder
  private OrderMenuMappingServiceResponse(String id, Boolean allChecked, List<MappingMenu> menus) {
    this.id = id;
    this.allChecked = allChecked;
    this.menus = menus;
  }

  public static OrderMenuMappingServiceResponse of(DisplayCategoryEntity displayCategory,
      List<DisplayMenuEntity> displayMenus) {
    return OrderMenuMappingServiceResponse.builder()
        .id(displayCategory.getCategoryId())
        .allChecked(displayCategory.isAllChecked())
        .menus(displayMenus.stream()
            .map(displayMenu -> MappingMenu.builder()
                .id(displayMenu.getMenuId())
                .name(displayMenu.getMenuName())
                .ordering(displayMenu.getOrdering())
                .isChecked(displayMenu.getIsChecked())
                .build()
            )
            .sorted()
            .toList()
        )
        .build();
  }

  @Getter
  public static class MappingMenu implements Comparable<MappingMenu> {

    private final String id;
    private final String name;
    private final Boolean isChecked;
    private final int ordering;

    @Builder
    private MappingMenu(String id, String name, Boolean isChecked, int ordering) {
      this.id = id;
      this.name = name;
      this.isChecked = isChecked;
      this.ordering = ordering;
    }

    @Override
    public int compareTo(MappingMenu o) {
      return ordering - o.ordering;
    }

  }

}
