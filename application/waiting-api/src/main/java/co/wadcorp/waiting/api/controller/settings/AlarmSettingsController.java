package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.model.settings.request.AlarmSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.AlarmSettingsResponse;
import co.wadcorp.waiting.api.service.settings.AlarmSettingsApiService;
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
public class AlarmSettingsController {

  private final AlarmSettingsApiService alarmSettingsApiService;

  /**
   * 웨이팅 알림 설정 조회
   * @param shopId
   * @return
   */
  @GetMapping(value = "/api/v1/shops/{shopId}/settings/waiting-alarm")
  public ApiResponse<AlarmSettingsResponse> getWaitingAlarmSettings(@PathVariable String shopId) {
    return ApiResponse.ok(alarmSettingsApiService.getWaitingAlarmSettings(shopId));
  }


  /**
   * 웨이팅 알림 설정 저장
   * @param shopId
   * @param request
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/settings/waiting-alarm")
  public ApiResponse<AlarmSettingsResponse> save(@PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody AlarmSettingsRequest request) {
    return ApiResponse.ok(alarmSettingsApiService.save(shopId, deviceId, request));
  }

}
