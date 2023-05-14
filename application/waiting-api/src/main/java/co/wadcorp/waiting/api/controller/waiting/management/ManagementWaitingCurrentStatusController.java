package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingCurrentStatusApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementWaitingCurrentStatusResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ManagementWaitingCurrentStatusController {

  private final ManagementWaitingCurrentStatusApiService service;

  /**
   * 웨이팅 현황, 운영 정보 조회
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/management/waiting/current-status")
  public ApiResponse<ManagementWaitingCurrentStatusResponse> currentStatusDefault(
      @PathVariable("shopId") String shopId
  ) {

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(
        service.getCurrentStatus(shopId, operationDate, nowDateTime)
    );
  }
}
