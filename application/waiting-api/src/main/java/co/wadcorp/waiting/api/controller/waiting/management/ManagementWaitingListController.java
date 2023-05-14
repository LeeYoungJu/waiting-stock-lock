package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.model.waiting.request.WaitingListRequest;
import co.wadcorp.waiting.api.model.waiting.response.WaitingInfoResponse;
import co.wadcorp.waiting.api.model.waiting.response.WaitingListResponse;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingInfoApiService;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingListApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ManagementWaitingListController {

  private final ManagementWaitingInfoApiService managementWaitingInfoApiService;
  private final ManagementWaitingListApiService managementWaitingListApiService;

  /**
   * 웨이팅 목록, 현황, 운영 정보 조회
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/management/waiting")
  public ApiResponse<WaitingInfoResponse> waitingInfo(
      @PathVariable("shopId") String shopId, @RequestHeader("X-CTM-AUTH") String ctmAuth,
      WaitingListRequest request) {

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(
        managementWaitingInfoApiService.getWaitingList(shopId, operationDate, nowDateTime, request)
    );
  }

  /**
   * 웨이팅 목록 조회
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/management/waiting/list")
  public ApiResponse<WaitingListResponse> waitingList(
      @PathVariable("shopId") String shopId, @RequestHeader("X-CTM-AUTH") String ctmAuth,
      WaitingListRequest request) {

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    return ApiResponse.ok(
        managementWaitingListApiService.getWaitingList(shopId, operationDate, request)
    );
  }

}
