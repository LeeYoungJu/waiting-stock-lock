package co.wadcorp.waiting.data.query.menu.dto;

import co.wadcorp.waiting.data.support.Price;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryIdMenuDto {

  private String categoryId;
  private String menuId;
  private String menuName;
  private Price unitPrice;

}
