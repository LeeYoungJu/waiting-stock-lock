package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.controller.waiting.management.dto.request.UpdateWaitingOrderRequest;
import co.wadcorp.waiting.api.service.waiting.management.ManagementUpdateWaitingOrderApiService;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingOrderApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingOrderResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ManagementWaitingOrderController {

  private final ManagementWaitingOrderApiService managementWaitingOrderApiService;
  private final ManagementUpdateWaitingOrderApiService managementUpdateWaitingOrderApiService;

  /**
   * 대시보드 - 주문정보 변경 조회 (모달)
   *
   * @param shopId
   * @param orderId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/management/orders/{orderId}")
  public ApiResponse<ManagementWaitingOrderResponse> getOrder(@PathVariable String shopId,
      @PathVariable String orderId) {
    return ApiResponse.ok(managementWaitingOrderApiService.getOrder(shopId, orderId,
        OperationDateUtils.getOperationDateFromNow()));
  }

  /**
   * 대시보드 - 주문정보 변경 수정 (모달)
   *
   * @param shopId
   * @param orderId
   * @param request
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/management/orders/{orderId}")
  public ApiResponse<ManagementWaitingOrderResponse> updateOrder(@PathVariable String shopId,
      @PathVariable String orderId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody UpdateWaitingOrderRequest request) {
    return ApiResponse.ok(
        managementUpdateWaitingOrderApiService.updateOrder(shopId, orderId, request.toServiceRequest(), deviceId));
  }

}
