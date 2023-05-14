package co.wadcorp.waiting.api.controller.waiting.web;

import co.wadcorp.waiting.api.service.waiting.web.WaitingChangeStatusWebService;
import co.wadcorp.waiting.api.service.waiting.web.WebCancelResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingWebChangeStatusController {

  private final WaitingChangeStatusWebService waitingChangeStatusWebService;

  /**
   * 캐치테이블 웨이팅 웹 취소
   *
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/web/v1/waiting/{waitingId}/cancel")
  public ApiResponse<WebCancelResponse> cancelWaiting(@PathVariable String waitingId) {

    return ApiResponse.ok(waitingChangeStatusWebService.cancel(waitingId,
        OperationDateUtils.getOperationDateFromNow()));
  }

  /**
   * 캐치테이블 웨이팅 웹 미루기
   *
   * @param waitingId
   * @return
   */
  @PostMapping(value = "/web/v1/waiting/{waitingId}/put-off")
  public ApiResponse<?> putOffWaiting(@PathVariable String waitingId) {

    waitingChangeStatusWebService.putOff(waitingId, OperationDateUtils.getOperationDateFromNow());
    return ApiResponse.ok();
  }
}
