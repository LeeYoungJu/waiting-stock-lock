package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.model.waiting.request.ChangeShopOperationStatusRequest;
import co.wadcorp.waiting.api.service.waiting.ShopOperationApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManagementShopOperationStatusController {

  private final ShopOperationApiService service;

  public ManagementShopOperationStatusController(ShopOperationApiService service) {
    this.service = service;
  }

  /**
   * 웨이팅 운영일 기준 웨이팅 운영 상태 변경 API
   * @param shopId
   * @param request
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/management/operation/change-status")
  public ApiResponse<?> changeStatus(
      @PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody ChangeShopOperationStatusRequest request) {
    service.changeOperationStatus(shopId, request, ZonedDateTimeUtils.nowOfSeoul(), deviceId);

    return ApiResponse.ok();
  }

}
