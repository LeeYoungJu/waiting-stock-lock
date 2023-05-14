package co.wadcorp.waiting.data.domain.order;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderLineItemStatus {

  CREATED("생성"),
  CHANGED("변경"),
  CANCELED("취소");

  private final String text;

}
