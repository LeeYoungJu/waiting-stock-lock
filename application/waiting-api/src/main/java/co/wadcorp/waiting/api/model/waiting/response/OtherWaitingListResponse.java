package co.wadcorp.waiting.api.model.waiting.response;

import static co.wadcorp.waiting.api.support.ExpectedWaitingPeriodConstant.MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.query.waiting.dto.WaitingOfOtherShopQueryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherWaitingListResponse {

  private String waitingId;
  private String shopId;
  private String shopName;
  private Integer waitingOrder; // N번째 (N = 남은 웨이팅 수 + 1)
  private Integer expectedWaitingPeriod;  // N * 팀당 웨이팅 예상시간
  private Integer maxExpressionWaitingPeriod; // MAX_EXPECTED_WAITING_PERIOD_CONSTANT 확인
  private String regDateTime;

  public OtherWaitingListResponse(WaitingOfOtherShopQueryDto dto, Integer waitingOrder,
      Integer expectedWaitingPeriodSetting) {
    BeanUtils.copyProperties(dto, this);
    this.expectedWaitingPeriod = expectedWaitingPeriodSetting == null ? null : expectedWaitingPeriodSetting * waitingOrder;
    this.waitingOrder = waitingOrder;
    this.regDateTime = ISO8601.format(dto.getRegDateTime());
    this.maxExpressionWaitingPeriod = MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;
  }
}
