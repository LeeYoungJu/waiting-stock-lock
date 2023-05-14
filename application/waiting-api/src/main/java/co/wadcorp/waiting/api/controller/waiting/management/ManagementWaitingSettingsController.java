package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.model.settings.response.ManagementSettingsResponse;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingSettingsApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ManagementWaitingSettingsController {

  private final ManagementWaitingSettingsApiService managementWaitingSettingsApiService;

  /**
   * 관리모드 필요한 설정 정보 조회
   *
   * @param shopId
   * @return 홈/옵션/운영/알람 설정
   */
  @GetMapping(value = "/api/v1/shops/{shopId}/management/waiting/settings")
  public ApiResponse<ManagementSettingsResponse> getAllManagementSettings(
      @PathVariable String shopId) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    return ApiResponse.ok(
        managementWaitingSettingsApiService.getAllManagementSettings(shopId, operationDate)
    );
  }
}
