package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.service.settings.dto.request.MenuType;
import co.wadcorp.waiting.api.service.waiting.management.ManagementDisplayMenuApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementOrderMenuResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ManagementMenuController {

  private final ManagementDisplayMenuApiService managementDisplayMenuApiService;

  /**
   * 대시보드 - 수기등록 메뉴 조회
   *
   * @param shopId
   * @param menuType
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/management/waiting/orders/menus")
  public ApiResponse<ManagementOrderMenuResponse> getOrder(@PathVariable String shopId,
      @RequestParam MenuType menuType
  ) {
    return ApiResponse.ok(
        managementDisplayMenuApiService.getDisplayMenu(shopId, menuType.getDisplayMappingType(),
            OperationDateUtils.getOperationDateFromNow())
    );
  }


}
