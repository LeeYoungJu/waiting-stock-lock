package co.wadcorp.waiting.data.query.displaymenu.dto;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import co.wadcorp.waiting.data.support.Price;
import lombok.Getter;

@Getter
public class DisplayMenuDto implements Comparable<DisplayMenuDto> {

  private String categoryId;
  private String menuId;
  private String menuName;
  private int ordering;
  private DisplayMappingType displayMappingType;
  private Price unitPrice;
  private Boolean isUsedDailyStock;
  private Integer dailyStock;
  private Boolean isUsedMenuQuantityPerTeam;
  private Integer menuQuantityPerTeam;

  public DisplayMenuDto() {
  }

  @Override
  public int compareTo(DisplayMenuDto o) {
    return ordering - o.ordering;
  }

}
