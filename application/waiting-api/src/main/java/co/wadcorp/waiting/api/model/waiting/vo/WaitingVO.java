package co.wadcorp.waiting.api.model.waiting.vo;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingCalledCountDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingVO {

    private final String waitingId;
    private final String shopId;
    private final RegisterChannel registerChannel;
    private final String registerChannelText;
    private final String operationDate;
    private final long customerSeq;
    private final String customerPhoneNumber;
    private final String customerName;
    private final int sittingCount;
    private final int waitingNumber;
    private final int waitingOrder;
    private final String waitingMemo;
    private final WaitingStatus waitingStatus;
    private final WaitingDetailStatus waitingDetailStatus;
    private final String seatOptionName;
    private final int totalSeatCount;
    private final List<PersonOption> personOptions;
    private final String personOptionText;
    private final String expectedSittingDateTime;
    private final String waitingCompleteDateTime;
    private final String calledDateTime;
    private final String lastCalledDateTime;
    private final long callCount;
    private final Boolean isSentReadyToEnterAlarm;
    private final String regDateTime;
    private final Order order;

    @Getter
    public static class Order {
      public static final Order EMPTY = Order.builder().build();

      private final String id;
      private final List<OrderLineItem> orderLineItems;

      @Builder
      private Order(String id, List<OrderLineItem> orderLineItems) {
        this.id = id;
        this.orderLineItems = orderLineItems;
      }
    }

    @Getter
    public static class OrderLineItem {

      private final String menuId;
      private final String name;
      private final int quantity;

      @Builder
      private OrderLineItem(String menuId, String name, int quantity) {
        this.menuId = menuId;
        this.name = name;
        this.quantity = quantity;
      }
    }

    @Builder
    public WaitingVO(String waitingId, String shopId, RegisterChannel registerChannel,
        LocalDate operationDate, long customerSeq,
        String customerPhoneNumber, String customerName, int sittingCount, int waitingNumber,
        int waitingOrder, String waitingMemo, WaitingStatus waitingStatus, WaitingDetailStatus waitingDetailStatus,
        String seatOptionName, int totalSeatCount, List<PersonOption> personOptions,
        String personOptionText, ZonedDateTime expectedSittingDateTime,
        ZonedDateTime waitingCompleteDateTime, ZonedDateTime calledDateTime, ZonedDateTime lastCalledDateTime, long callCount,
        boolean isSentReadyToEnterAlarm, ZonedDateTime regDateTime, Order order) {
      this.waitingId = waitingId;
      this.shopId = shopId;
      this.registerChannel = registerChannel;
      this.registerChannelText = registerChannel.getValue();
      this.operationDate = operationDate.toString();
      this.customerSeq = customerSeq;
      this.customerPhoneNumber = customerPhoneNumber;
      this.customerName = customerName;
      this.sittingCount = sittingCount;
      this.waitingNumber = waitingNumber;
      this.waitingOrder = waitingOrder;
      this.waitingMemo = waitingMemo;
      this.waitingStatus = waitingStatus;
      this.waitingDetailStatus = waitingDetailStatus;
      this.seatOptionName = seatOptionName;
      this.totalSeatCount = totalSeatCount;
      this.personOptions = personOptions;
      this.personOptionText = personOptionText;

      this.expectedSittingDateTime = ISO8601.format(expectedSittingDateTime);
      this.waitingCompleteDateTime = ISO8601.format(waitingCompleteDateTime);
      this.calledDateTime = ISO8601.format(calledDateTime);
      this.lastCalledDateTime = ISO8601.format(lastCalledDateTime);
      this.callCount = callCount;
      this.isSentReadyToEnterAlarm = isSentReadyToEnterAlarm;
      this.regDateTime = ISO8601.format(regDateTime);
      this.order = order;
    }

    public static WaitingVO toDto(
        WaitingDto dto,
        WaitingCalledCountDto waitingCalledCountDto,
        boolean isSentReadyToEnterAlarm,
        WaitingOrderDto waitingOrderDto
    ) {
      return WaitingVO.builder()
          .waitingId(dto.getWaitingId())
          .shopId(dto.getShopId())
          .registerChannel(dto.getRegisterChannel())
          .operationDate(dto.getOperationDate())
          .customerSeq(dto.getCustomerSeq())
          .customerPhoneNumber(dto.getCustomerPhoneNumber())
          .customerName(dto.getCustomerName())
          .sittingCount(dto.getSittingCount() + 1)   // 몇번째 방문인가는 (착석횟수 + 1)로 계산하기로 결정
          .waitingNumber(dto.getWaitingNumber())
          .waitingOrder(dto.getWaitingOrder())
          .waitingMemo(dto.getWaitingMemo())
          .waitingStatus(dto.getWaitingStatus())
          .waitingDetailStatus(dto.getWaitingDetailStatus())
          .seatOptionName(dto.getSeatOptionName())
          .totalSeatCount(dto.getTotalSeatCount())
          .personOptions(dto.getPersonOptions())
          .personOptionText(dto.getPersonOptionText())
          .expectedSittingDateTime(dto.getExpectedSittingDateTime())
          .waitingCompleteDateTime(dto.getWaitingCompleteDateTime())
          .calledDateTime(waitingCalledCountDto.getCalledDateTime())
          .lastCalledDateTime(waitingCalledCountDto.getCalledDateTime())
          .callCount(waitingCalledCountDto.getCallCount())
          .isSentReadyToEnterAlarm(isSentReadyToEnterAlarm)
          .regDateTime(dto.getRegDateTime())
          .order(convertOrder(waitingOrderDto))
          .build();
    }

    private static Order convertOrder(WaitingOrderDto waitingOrderDto) {
      if (Objects.isNull(waitingOrderDto) || WaitingOrderDto.EMPTY_ORDER == waitingOrderDto) {
        return Order.EMPTY;
      }

      return Order.builder()
          .id(waitingOrderDto.getOrderId())
          .orderLineItems(waitingOrderDto.getOrderLineItems()
              .stream()
              .filter(WaitingOrderDto.OrderLineItem::isNotCanceledItem)
              .map(item ->
                  OrderLineItem.builder()
                      .menuId(item.getMenuId())
                      .name(item.getMenuName())
                      .quantity(item.getQuantity())
                      .build()
              )
              .toList())
          .build();
    }
}
