package co.wadcorp.waiting.api.internal.controller.waiting.dto.request;

import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteOrderDto;
import co.wadcorp.waiting.data.domain.stock.MenuQuantity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteOrderRequest {

  @NotNull(message = "주문의 총 가격은 필수입니다.")
  private BigDecimal totalPrice;

  @NotNull(message = "메뉴 선택은 필수입니다.")
  @Valid
  private List<OrderLineItem> orderLineItems;

  public RemoteOrderRequest() {
  }

  @Builder
  private RemoteOrderRequest(BigDecimal totalPrice, List<OrderLineItem> orderLineItems) {
    this.totalPrice = totalPrice;
    this.orderLineItems = orderLineItems;
  }

  @Getter
  public static class OrderLineItem {

    @NotBlank(message = "메뉴 아이디는 필수입니다.")
    private String menuId;

    @NotBlank(message = "메뉴명은 필수입니다.")
    private String name;

    @NotNull(message = "메뉴의 가격은 필수입니다.")
    private BigDecimal unitPrice;

    @NotNull(message = "메뉴별 가격 합계는 필수입니다.")
    private BigDecimal linePrice;

    @Positive(message = "주문수량은 1 이상이어야 합니다.")
    private int quantity;

    public OrderLineItem() {
    }

    @Builder
    private OrderLineItem(String menuId, String name, BigDecimal unitPrice, BigDecimal linePrice, int quantity) {
      this.menuId = menuId;
      this.name = name;
      this.unitPrice = unitPrice;
      this.linePrice = linePrice;
      this.quantity = quantity;
    }
  }

  public RemoteOrderDto toRemoteOrderDto() {
    return RemoteOrderDto.builder()
        .totalPrice(totalPrice)
        .orderLineItems(orderLineItems.stream()
                .map(item -> RemoteOrderDto.OrderLineItem.builder()
                    .menuId(item.menuId)
                    .name(item.name)
                    .unitPrice(item.unitPrice)
                    .linePrice(item.linePrice)
                    .quantity(item.quantity)
                    .build()
                )
                .toList()
            )
        .build();

  }
}
