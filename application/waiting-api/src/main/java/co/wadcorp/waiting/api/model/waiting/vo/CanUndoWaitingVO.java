package co.wadcorp.waiting.api.model.waiting.vo;

import static co.wadcorp.waiting.api.support.ExpectedWaitingPeriodConstant.MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.domain.waiting.RegisterChannel;
import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingDto;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CanUndoWaitingVO {

  private final String shopName;
  private final String waitingId;
  private final RegisterChannel registerChannel;
  private final Integer waitingNumber;  // 채번
  private final Integer waitingOrder;   // N번째
  private final Integer expectedWaitingPeriod;
  private final Integer maxExpressionWaitingPeriod;
  private final String seatOptionName;
  private final String regDateTime;
  private final Boolean canUndo;

  @Builder
  private CanUndoWaitingVO(String shopName, String waitingId, RegisterChannel registerChannel,
      Integer waitingNumber, Integer waitingOrder,
      Integer expectedWaitingPeriod, String seatOptionName, ZonedDateTime regDateTime, Boolean canUndo) {
    this.shopName = shopName;
    this.waitingId = waitingId;
    this.registerChannel = registerChannel;
    this.waitingNumber = waitingNumber;
    this.waitingOrder = waitingOrder;
    this.expectedWaitingPeriod = expectedWaitingPeriod;
    this.maxExpressionWaitingPeriod = MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;
    this.seatOptionName = seatOptionName;
    this.regDateTime = ISO8601.format(regDateTime);
    this.canUndo = canUndo;
  }

  public static CanUndoWaitingVO toDto(WaitingDto item, String shopName, Integer teamCount,
      Integer expectedWaitingPeriod,
      long countDownMinute) {
    return CanUndoWaitingVO.builder()
        .shopName(shopName)
        .waitingId(item.getWaitingId())
        .registerChannel(item.getRegisterChannel())
        .waitingNumber(item.getWaitingNumber())
        .waitingOrder(teamCount)
        .expectedWaitingPeriod(expectedWaitingPeriod)
        .seatOptionName(item.getSeatOptionName())
        .regDateTime(item.getRegDateTime())
        .canUndo(isCanUndo(item.getWaitingDetailStatus(), countDownMinute))
        .build();
  }

  private static boolean isCanUndo(WaitingDetailStatus waitingDetailStatus, long diffMinute) {
    return WaitingDetailStatus.CANCEL_BY_SITTING == waitingDetailStatus && diffMinute < 30;
  }

}
