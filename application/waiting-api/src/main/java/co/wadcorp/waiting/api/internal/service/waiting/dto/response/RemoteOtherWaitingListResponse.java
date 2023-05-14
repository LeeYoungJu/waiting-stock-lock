package co.wadcorp.waiting.api.internal.service.waiting.dto.response;

import static co.wadcorp.waiting.api.support.ExpectedWaitingPeriodConstant.MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;

import co.wadcorp.libs.datetime.ISO8601;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteOtherWaitingListResponse {

  private final String waitingId;
  private final Long shopId;
  private final String shopName;
  private final Integer waitingOrder; // N번째 (N = 남은 웨이팅 수 + 1)
  private final Integer expectedWaitingPeriod;  // N * 팀당 웨이팅 예상시간
  private final Integer maxExpressionWaitingPeriod; // MAX_EXPECTED_WAITING_PERIOD_CONSTANT 확인
  private final Boolean isTakeOut;
  private final String regDateTime;

  @Builder
  public RemoteOtherWaitingListResponse(String waitingId, Long shopId, String shopName,
      Integer waitingOrder, Integer expectedWaitingPeriod, Boolean isTakeOut, ZonedDateTime regDateTime) {
    this.waitingId = waitingId;
    this.shopId = shopId;
    this.shopName = shopName;
    this.waitingOrder = waitingOrder;
    this.expectedWaitingPeriod = expectedWaitingPeriod == null ? null : expectedWaitingPeriod * waitingOrder;
    this.isTakeOut = isTakeOut;
    this.maxExpressionWaitingPeriod = MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;
    this.regDateTime = ISO8601.format(regDateTime);
  }

}
