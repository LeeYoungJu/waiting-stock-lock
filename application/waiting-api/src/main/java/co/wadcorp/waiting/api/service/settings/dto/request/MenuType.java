package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.displaymenu.DisplayMappingType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MenuType {

  SHOP_MENU("매장"),
  TAKE_OUT_MENU("포장");

  private final String text;

  public DisplayMappingType getDisplayMappingType() {
    return this == SHOP_MENU
        ? DisplayMappingType.SHOP
        : DisplayMappingType.TAKE_OUT;
  }

}
