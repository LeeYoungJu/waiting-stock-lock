package co.wadcorp.waiting.api.internal.service.waiting.dto.request;

import static org.junit.jupiter.api.Assertions.*;

import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteOrderRequest.OrderLineItem;
import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemoteOrderDtoTest {

  @DisplayName("주문 메뉴별 가격 정보가 잘못되었으면 isValidLineItemsPrice는 false를 반환한다.")
  @Test
  void isValidLineItemsPriceWithInvalidLineItemsPrice() {
    // given
    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(300))
        .orderLineItems(List.of(
            RemoteOrderDto.OrderLineItem.builder()
                .menuId("MENU_ID")
                .name("메뉴")
                .unitPrice(BigDecimal.valueOf(100))
                .linePrice(BigDecimal.valueOf(200))
                .quantity(3)
                .build()
        ))
        .build();

    // when // then
    assertFalse(
        orderDto.isLineItemsPriceValid()
    );
  }

  @DisplayName("주문 메뉴별 가격 정보가 문제 없으면 isValidLineItemsPrice는 true를 반환한다.")
  @Test
  void isValidLineItemsPriceWithNormal() {
    // given
    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(200))
        .orderLineItems(List.of(
            RemoteOrderDto.OrderLineItem.builder()
                .menuId("MENU_ID")
                .name("메뉴")
                .unitPrice(BigDecimal.valueOf(100))
                .linePrice(BigDecimal.valueOf(200))
                .quantity(2)
                .build()
        ))
        .build();

    // when // then
    assertTrue(
        orderDto.isLineItemsPriceValid()
    );
  }

  @DisplayName("최종 가격(total price)가 잘못되었으면 isValidTotalPrice는 false를 반환한다.")
  @Test
  void isValidTotalPriceWithInvalidTotalPrice() {
    // given
    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(600))
        .orderLineItems(List.of(
            RemoteOrderDto.OrderLineItem.builder()
                .menuId("MENU_ID_1")
                .name("메뉴1")
                .unitPrice(BigDecimal.valueOf(100))
                .linePrice(BigDecimal.valueOf(200))
                .quantity(2)
                .build(),

            RemoteOrderDto.OrderLineItem.builder()
                .menuId("MENU_ID_2")
                .name("메뉴2")
                .unitPrice(BigDecimal.valueOf(300))
                .linePrice(BigDecimal.valueOf(300))
                .quantity(1)
                .build()
        ))
        .build();

    // when // then
    assertFalse(
        orderDto.isTotalPriceValid()
    );
  }

  @DisplayName("최종 가격(total price)가 문제 없으면 isValidTotalPrice는 true를 반환한다.")
  @Test
  void isValidTotalPriceWithNormal() {
    // given
    RemoteOrderDto orderDto = RemoteOrderDto.builder()
        .totalPrice(BigDecimal.valueOf(500))
        .orderLineItems(List.of(
            RemoteOrderDto.OrderLineItem.builder()
                .menuId("MENU_ID_1")
                .name("메뉴1")
                .unitPrice(BigDecimal.valueOf(100))
                .linePrice(BigDecimal.valueOf(200))
                .quantity(2)
                .build(),

            RemoteOrderDto.OrderLineItem.builder()
                .menuId("MENU_ID_2")
                .name("메뉴2")
                .unitPrice(BigDecimal.valueOf(300))
                .linePrice(BigDecimal.valueOf(300))
                .quantity(1)
                .build()
        ))
        .build();

    // when // then
    assertTrue(
        orderDto.isTotalPriceValid()
    );
  }

}