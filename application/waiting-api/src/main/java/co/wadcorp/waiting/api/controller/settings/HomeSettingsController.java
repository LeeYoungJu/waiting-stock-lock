package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.model.settings.request.HomeSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.HomeSettingsResponse;
import co.wadcorp.waiting.api.service.settings.HomeSettingsApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeSettingsController {

  private final HomeSettingsApiService homeSettingsApiService;

  /**
   * 웨이팅 홈 설정 조회
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/waiting-home")
  public ApiResponse<HomeSettingsResponse> getWaitingHomeSettings(@PathVariable String shopId) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowZonedDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(
        homeSettingsApiService.getHomeSettings(shopId, operationDate, nowZonedDateTime));
  }

  /**
   * 웨이팅 홈 설정 저장
   *
   * @param shopId
   * @param homeSettingsRequest
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/waiting-home")
  public ApiResponse<HomeSettingsResponse> saveWaitingHomeSettings(@PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody HomeSettingsRequest homeSettingsRequest) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowZonedDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(
        homeSettingsApiService.saveHomeSettings(shopId, deviceId, homeSettingsRequest, operationDate,
            nowZonedDateTime));
  }
}
