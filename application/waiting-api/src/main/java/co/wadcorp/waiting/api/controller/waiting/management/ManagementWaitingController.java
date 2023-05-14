package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.model.waiting.response.WaitingResponse;
import co.wadcorp.waiting.api.service.waiting.WaitingApiService;
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
public class ManagementWaitingController {

  private final WaitingApiService service;

  /**
   * 웨이팅 단건 조회
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/management/waiting/{waitingId}")
  public ApiResponse<WaitingResponse> currentStatusDefault(
      @PathVariable("shopId") String shopId, @PathVariable("waitingId") String waitingId,
      @RequestHeader("X-CTM-AUTH") String ctmAuth
  ) {
    return ApiResponse.ok(service.getWaitingBy(waitingId));
  }
}
