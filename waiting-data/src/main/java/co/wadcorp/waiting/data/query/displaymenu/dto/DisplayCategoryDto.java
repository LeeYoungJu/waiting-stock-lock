package co.wadcorp.waiting.data.query.displaymenu.dto;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DisplayCategoryDto implements Comparable<DisplayCategoryDto> {

  private Long seq;
  private String categoryId;
  private String categoryName;
  private int ordering;
  private DisplayMappingType displayMappingType;
  private boolean isAllChecked;

  @Override
  public int compareTo(DisplayCategoryDto o) {
    return ordering - o.ordering;
  }

}
