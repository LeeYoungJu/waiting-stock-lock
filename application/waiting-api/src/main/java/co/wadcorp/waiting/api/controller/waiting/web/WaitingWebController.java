package co.wadcorp.waiting.api.controller.waiting.web;

import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WaitingWebResponse;
import co.wadcorp.waiting.api.controller.waiting.web.dto.response.WebCustomerWaitingListResponse;
import co.wadcorp.waiting.api.service.waiting.web.WaitingWebService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingWebController {

  private final WaitingWebService waitingWebService;

  /**
   * 캐치테이블 웨이팅 웹 현황 조회
   *
   * @param waitingId
   * @return 웨이팅 현황
   */
  @GetMapping(value = "/web/v1/waiting/{waitingId}")
  public ApiResponse<WaitingWebResponse> getWaitingInfo(@PathVariable String waitingId) {
    return ApiResponse.ok(
        waitingWebService.getWaitingInfo(waitingId, OperationDateUtils.getOperationDateFromNow())
    );
  }

  /**
   * 캐치테이블 웨이팅 웹 목록 조회
   *
   * @param waitingId
   * @return 나의 웨이팅 목록
   */
  @GetMapping(value = "/web/v1/waiting/{waitingId}/list")
  public ApiResponse<WebCustomerWaitingListResponse> getAllOtherWaitingByCustomer(
      @PathVariable String waitingId) {
    return ApiResponse.ok(waitingWebService.getAllCustomerWaitingByWaitingId(
        waitingId, OperationDateUtils.getOperationDateFromNow()
    ));
  }


}
