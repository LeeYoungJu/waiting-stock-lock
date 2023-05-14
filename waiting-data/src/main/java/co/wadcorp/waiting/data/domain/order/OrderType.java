package co.wadcorp.waiting.data.domain.order;

import co.wadcorp.waiting.data.domain.settings.SeatOptions;

public enum OrderType {

  SHOP("매장"),
  TAKE_OUT("포장");

  private final String text;

  OrderType(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public static OrderType findBy(boolean isTakeOut) {
    return isTakeOut ? OrderType.TAKE_OUT : OrderType.SHOP;
  }
}
