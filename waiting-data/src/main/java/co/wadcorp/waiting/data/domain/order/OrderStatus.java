package co.wadcorp.waiting.data.domain.order;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderStatus {

  CREATED("주문 생성"),
  CANCEL("주문 취소");

  private final String text;

}
