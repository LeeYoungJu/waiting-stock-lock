package co.wadcorp.waiting.api.controller.waiting.web.dto.response;

import static co.wadcorp.waiting.api.support.ExpectedWaitingPeriodConstant.MAX_EXPRESSION_WAITING_PERIOD_CONSTANT;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.libs.phone.PhoneNumber;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.query.waiting.dto.WebWaitingDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebCustomerWaitingListResponse {

  private String customerPhone;
  private List<WebCustomerWaiting> waitingList;

  public WebCustomerWaitingListResponse(PhoneNumber customerPhone,
      List<WebCustomerWaiting> waitingList) {
    this.customerPhone = customerPhone.getLocal();
    this.waitingList = waitingList;
  }

  @Getter
  @Builder
  public static class WebCustomerWaiting {

    private String waitingId;
    private String shopName;
    private String seatOptionName;
    private Integer waitingOrder; // N번째 (N = 남은 웨이팅 수 + 1)
    private Integer expectedWaitingPeriod;  // N * 팀당 웨이팅 예상시간
    private Integer maxExpressionWaitingPeriod;
    private String regDateTime;

    public static WebCustomerWaiting toDto(WebWaitingDto waiting, HomeSettingsData homeSettingsData,
        Integer waitingOrder, Integer expectedWaitingPeriodSetting) {
      return WebCustomerWaiting.builder()
          .waitingId(waiting.getWaitingId())
          .shopName(waiting.getShopName())
          .seatOptionName(
              homeSettingsData.isDefaultMode()
                  ? null
                  : waiting.getSeatOptionName()
          )
          .waitingOrder(waitingOrder)
          .expectedWaitingPeriod(
              expectedWaitingPeriodSetting == null
                  ? null
                  : expectedWaitingPeriodSetting * waitingOrder
          )
          .maxExpressionWaitingPeriod(MAX_EXPRESSION_WAITING_PERIOD_CONSTANT)
          .regDateTime(ISO8601.format(waiting.getRegDateTime()))
          .build();
    }
  }

}
