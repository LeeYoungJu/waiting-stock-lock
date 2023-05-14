package co.wadcorp.waiting.api.controller.waiting.register;

import co.wadcorp.waiting.api.model.waiting.request.CancelWaitingRequest;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterWaitingChangeStatusController {

  private final WaitingRegisterApiService waitingRegisterApiService;

  /**
   * 웨이팅 다중 취소
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/register/waiting/cancel")
  public ApiResponse<?> cancelWaiting(@PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody CancelWaitingRequest cancelWaitingRequest) {

    waitingRegisterApiService.cancelWaitingByIdList(cancelWaitingRequest,
        OperationDateUtils.getOperationDateFromNow(), deviceId);
    return ApiResponse.ok();
  }

}
