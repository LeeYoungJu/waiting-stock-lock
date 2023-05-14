package co.wadcorp.waiting.data.query.displaymenu.dto;

import co.wadcorp.waiting.data.support.Price;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DisplayMenuNamePriceDto implements Comparable<DisplayMenuNamePriceDto> {

  private Long seq;
  private String categoryId;
  private int categoryOrdering;
  private String menuId;
  private String name;
  private Price unitPrice;
  private int menuOrdering;
  private boolean isChecked;

  @Override
  public int compareTo(DisplayMenuNamePriceDto o) {
    return getOrderingNumber() - o.getOrderingNumber();
  }

  private int getOrderingNumber() {
    return 10 * categoryOrdering + menuOrdering;
  }

}
