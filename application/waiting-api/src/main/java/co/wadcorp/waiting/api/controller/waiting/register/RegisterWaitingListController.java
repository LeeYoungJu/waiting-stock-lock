package co.wadcorp.waiting.api.controller.waiting.register;

import co.wadcorp.waiting.api.model.waiting.request.PageRequestParams;
import co.wadcorp.waiting.api.model.waiting.response.OtherWaitingListResponse;
import co.wadcorp.waiting.api.model.waiting.response.RegisterWaitingListResponse;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterApiService;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterOtherWaitingService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterWaitingListController {

  private final WaitingRegisterApiService waitingRegisterApiService;
  private final WaitingRegisterOtherWaitingService waitingRegisterOtherWaitingService;

  /**
   * 타매장 웨이팅 목록 조회
   * <p>
   * 3개를 초과하여 웨이팅 등록을 시도할 때, 조회되는 API
   *
   * @param customerPhone
   * @return 웨이팅중인 리스트
   */
  @GetMapping(value = "/api/v1/shops/{shopId}/register/waiting/others")
  public ApiResponse<List<OtherWaitingListResponse>> getAllWaitingOfOtherShop(
      @PathVariable String shopId, @RequestParam String customerPhone) {
    return ApiResponse.ok(
        waitingRegisterOtherWaitingService.getAllWaitingOfOtherShopByCustomerPhone(
            shopId, customerPhone, OperationDateUtils.getOperationDateFromNow())
    );
  }

  /**
   * 웨이팅 목록 조회
   *
   * @param shopId
   * @param request
   */
  @GetMapping(value = "/api/v1/shops/{shopId}/register/waiting/list")
  public ApiResponse<RegisterWaitingListResponse> getAllWaiting(@PathVariable String shopId,
      PageRequestParams request) {
    LocalDate operationDateFromNow = OperationDateUtils.getOperationDateFromNow();
    return ApiResponse.ok(
        waitingRegisterApiService.getAllWaiting(shopId, request, operationDateFromNow)
    );
  }

}
