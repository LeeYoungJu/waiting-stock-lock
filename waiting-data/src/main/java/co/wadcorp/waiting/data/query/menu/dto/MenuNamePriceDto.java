package co.wadcorp.waiting.data.query.menu.dto;

import co.wadcorp.waiting.data.support.Price;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuNamePriceDto implements Comparable<MenuNamePriceDto> {

  private Long seq;
  private String categoryId;
  private int categoryOrdering;
  private String menuId;
  private String name;
  private Price unitPrice;
  private int menuOrdering;

  @Override
  public int compareTo(MenuNamePriceDto o) {
    return getOrderingNumber() - o.getOrderingNumber();
  }

  private int getOrderingNumber() {
    return 10 * categoryOrdering + menuOrdering;
  }

}
