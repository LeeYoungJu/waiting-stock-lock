package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.model.settings.request.OptionSettingsRequest;
import co.wadcorp.waiting.api.model.settings.response.OptionSettingsResponse;
import co.wadcorp.waiting.api.service.settings.OptionSettingsApiService;
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
public class OptionSettingsController {

  private final OptionSettingsApiService optionSettingsApiService;

  /**
   * 웨이팅 옵션 설정 조회
   * @param shopId
   * @return
   */
  @GetMapping(value = "/api/v1/shops/{shopId}/settings/waiting-option")
  public ApiResponse<OptionSettingsResponse> getWaitingOptionSettings(@PathVariable String shopId) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowZonedDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(optionSettingsApiService.getWaitingOptionSettings(shopId, operationDate, nowZonedDateTime));
  }


  /**
   * 웨이팅 옵션 설정 저장
   * @param shopId
   * @param request
   * @return
   */
  @PostMapping(value = "/api/v1/shops/{shopId}/settings/waiting-option")
  public ApiResponse<OptionSettingsResponse> save(@PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @RequestBody OptionSettingsRequest request) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowZonedDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(optionSettingsApiService.save(shopId, deviceId, request, operationDate, nowZonedDateTime));
  }

}
