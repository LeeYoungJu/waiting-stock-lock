package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.controller.waiting.management.dto.request.WaitingRegisterByManagerRequest;
import co.wadcorp.waiting.api.model.waiting.response.WaitingRegisterByManagerResponse;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingRegisterApiService;
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
public class ManagementWaitingRegisterController {

  private final ManagementWaitingRegisterApiService managementWaitingRegisterApiService;


  /**
   * 매장 웨이팅 등록
   *
   * @param shopId
   * @param waitingRegisterByManagerRequest
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/management/waiting")
  public ApiResponse<WaitingRegisterByManagerResponse> registerByManager(
      @PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody WaitingRegisterByManagerRequest waitingRegisterByManagerRequest) {
    return ApiResponse.ok(
        managementWaitingRegisterApiService.registerByManager(
            shopId,
            OperationDateUtils.getOperationDateFromNow(),
            waitingRegisterByManagerRequest,
            deviceId
        ));
  }

}
