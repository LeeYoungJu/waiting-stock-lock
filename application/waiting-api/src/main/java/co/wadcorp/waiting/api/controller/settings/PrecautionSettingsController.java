package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.model.settings.request.PrecautionSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.PrecautionSettingsResponse;
import co.wadcorp.waiting.api.service.settings.PrecautionSettingsApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PrecautionSettingsController {

  private final PrecautionSettingsApiService precautionSettingsApiService;

  /**
   * 웨이팅 유의사항 설정 조회
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/waiting-precaution")
  public ApiResponse<PrecautionSettingsResponse> getWaitingHomeSettings(
      @PathVariable String shopId) {

    return ApiResponse.ok(precautionSettingsApiService.getPrecautionSettings(shopId));
  }

  /**
   * 웨이팅 유의사항 설정 저장
   *
   * @param shopId
   * @param precautionSettingsRequest
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/waiting-precaution")
  public ApiResponse<PrecautionSettingsResponse> saveWaitingHomeSettings(
      @PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody PrecautionSettingsRequest precautionSettingsRequest) {

    return ApiResponse.ok(
        precautionSettingsApiService.savePrecautionSettings(shopId, deviceId, precautionSettingsRequest));
  }
}
