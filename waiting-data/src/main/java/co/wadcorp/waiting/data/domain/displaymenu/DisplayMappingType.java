package co.wadcorp.waiting.data.domain.displaymenu;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DisplayMappingType {

  SHOP("매장"),
  TAKE_OUT("포장");

  private final String text;

}
