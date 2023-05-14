package co.wadcorp.waiting.api.controller.waiting.register;

import co.wadcorp.waiting.api.model.settings.response.RegisterSettingsResponse;
import co.wadcorp.waiting.api.model.waiting.response.RegisterCurrentStatusResponse;
import co.wadcorp.waiting.api.service.waiting.WaitingRegisterIntroApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterWaitingIntroController {

  private final WaitingRegisterIntroApiService waitingRegisterIntroApiService;

  /**
   * 웨이팅 등록 메인 정보 조회
   *
   * @param shopId
   * @return 웨이팅 등록 메인 정보
   */
  @GetMapping(value = "/api/v1/shops/{shopId}/register/waiting/current-status")
  public ApiResponse<RegisterCurrentStatusResponse> getWaitingCurrentStatus(
      @PathVariable String shopId,
      @RequestHeader("X-CTM-AUTH") String ctmAuth,
      @RequestParam(required = false, defaultValue = "DEFAULT") String modeType) {

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowDateTime = ZonedDateTimeUtils.nowOfSeoul();

    if ("TABLE".equals(modeType)) {
      return ApiResponse.ok(waitingRegisterIntroApiService.getTableCurrentStatus(shopId,
          operationDate, nowDateTime));
    }
    return ApiResponse.ok(waitingRegisterIntroApiService.getDefaultCurrentStatus(shopId,
        operationDate, nowDateTime));

  }

  /**
   * 등록모드 필요한 설정 정보 조회
   *
   * @param shopId
   * @return 홈/옵션 설정
   */
  @GetMapping(value = "/api/v1/shops/{shopId}/register/waiting/settings")
  public ApiResponse<RegisterSettingsResponse> getAllRegisterSettings(@PathVariable String shopId) {
    return ApiResponse.ok(waitingRegisterIntroApiService.getAllRegisterSettings(shopId));
  }


}
