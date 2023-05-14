package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.model.settings.request.OperationTimeSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.OperationTimeSettingsResponse;
import co.wadcorp.waiting.api.service.settings.OperationTimeSettingsApiService;
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
public class OperationSettingsController {

  private final OperationTimeSettingsApiService operationTimeSettingsApiService;

  /**
   * 웨이팅 운영 시간 설정 조회
   *
   * @param shopId
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/waiting-operation-time")
  public ApiResponse<OperationTimeSettingsResponse> getOperationTimeSettings(
      @PathVariable String shopId) {
    return ApiResponse.ok(operationTimeSettingsApiService.getOperationTimeSettings(shopId));
  }

  /**
   * 웨이팅 운영 시간 설정 저장
   *
   * @param shopId
   * @param operationTimeSettingsRequest
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/waiting-operation-time")
  public ApiResponse<OperationTimeSettingsResponse> saveOperationTImeSettings(
      @PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody OperationTimeSettingsRequest operationTimeSettingsRequest) {
    return ApiResponse.ok(operationTimeSettingsApiService.saveOperationTimeSettings(shopId, deviceId,
        operationTimeSettingsRequest));
  }
}
