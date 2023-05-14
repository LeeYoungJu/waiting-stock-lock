package co.wadcorp.waiting.api.controller.waiting.web.dto.response;

import static co.wadcorp.waiting.api.support.ExpectedWaitingPeriodConstant.MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.OrderDto.OrderLineItemDto;
import co.wadcorp.waiting.api.model.settings.vo.PrecautionVO;
import co.wadcorp.waiting.api.model.waiting.vo.WebPersonOptionVO;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.waiting.PersonOption;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.domain.waiting.WebProgressMessage;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto;
import co.wadcorp.waiting.data.query.order.dto.WaitingOrderDto.OrderLineItem;
import co.wadcorp.waiting.data.query.shop.dto.ShopDto;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingHistoriesDto.WaitingDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingWebResponse {

  private String shopName;
  private String shopAddress;
  private String shopTelNumber;

  private WaitingStatus waitingStatus;
  private WaitingDetailStatus waitingDetailStatus;

  private Integer waitingNumber;  // 채번
  private Integer waitingOrder;   // N번째

  private Integer expectedWaitingPeriod;
  private Integer maxExpressionWaitingPeriod;
  private String seatOptionName;
  private String regDateTime;

  private Integer totalPersonCount;
  private List<WebPersonOptionVO> personOptions;

  private List<PrecautionVO> precautions;
  private long canPutOffCount;
  private String message;

  private OrderDto order;

  private boolean disablePutOff = false;

  public static WaitingWebResponse toDto(WaitingDto waiting, ShopDto shopDto,
      Integer waitingTeamCount,
      Integer expectedWaitingPeriod, long canPutOffCount, List<Precaution> precautions,
      WaitingOrderDto waitingOrderDto, boolean disablePutOff
  ) {
    return WaitingWebResponse.builder()
        .shopName(shopDto.getShopName())
        .shopAddress(shopDto.getShopAddress())
        .shopTelNumber(shopDto.getShopTelNumber())
        .waitingDetailStatus(waiting.getWaitingDetailStatus())
        .waitingStatus(waiting.getWaitingStatus())
        .waitingNumber(waiting.getWaitingNumber())
        .waitingOrder(waitingTeamCount)
        .expectedWaitingPeriod(expectedWaitingPeriod)
        .regDateTime(ISO8601.format(waiting.getRegDateTime()))
        .totalPersonCount(waiting.getTotalPersonCount())
        .personOptions(convertToPersonOptionVOs(waiting.getPersonOptions()))
        .seatOptionName(waiting.getSeatOptionName())
        .maxExpressionWaitingPeriod(MAX_EXPRESSION_WAITING_PERIOD_CONSTANT)
        .precautions(convertToPrecautionVOs(precautions))
        .message(WebProgressMessage.getMessage(waiting.getWaitingDetailStatus()))
        .canPutOffCount(canPutOffCount)
        .order(convertToOrderVO(waitingOrderDto))
        .disablePutOff(disablePutOff)
        .build();
  }

  private static OrderDto convertToOrderVO(WaitingOrderDto waitingOrderDto) {
    if (waitingOrderDto == WaitingOrderDto.EMPTY_ORDER) {
      return null;
    }

    return OrderDto.builder()
        .totalPrice(waitingOrderDto.getTotalPrice().value())
        .orderLineItems(
            waitingOrderDto.getOrderLineItems()
                .stream()
                .filter(OrderLineItem::isNotCanceledItem)
                .map(OrderLineItemDto::of)
                .toList())
        .build();
  }

  private static List<WebPersonOptionVO> convertToPersonOptionVOs(
      List<PersonOption> personOptions) {
    return personOptions.stream()
        .map(WebPersonOptionVO::new)
        .toList();
  }

  private static List<PrecautionVO> convertToPrecautionVOs(List<Precaution> precautions) {
    return precautions.stream()
        .map(PrecautionVO::toDto)
        .toList();
  }

}
