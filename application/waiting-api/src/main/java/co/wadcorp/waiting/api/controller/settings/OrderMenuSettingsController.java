package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.controller.settings.dto.request.OrderMenuSettingsRequest;
import co.wadcorp.waiting.api.service.settings.OrderMenuSettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuSettingsResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderMenuSettingsController {

  private final OrderMenuSettingsApiService orderMenuSettingsApiService;

  /**
   * 설정 - 메뉴 리스트 조회
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/orders/menus")
  public ApiResponse<OrderMenuSettingsListResponse> getOrderMenus(@PathVariable String shopId) {
    return ApiResponse.ok(orderMenuSettingsApiService.getMenus(shopId));
  }

  /**
   * 설정 - 메뉴 단건 조회 (모달)
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}")
  public ApiResponse<OrderMenuSettingsResponse> getOrderMenu(@PathVariable String shopId,
      @PathVariable String menuId) {
    return ApiResponse.ok(orderMenuSettingsApiService.getMenu(menuId));
  }

  /**
   * 설정 - 메뉴 단건 생성 (모달)
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders/menus")
  public ApiResponse<OrderMenuSettingsResponse> createMenu(@PathVariable String shopId,
      @Valid @RequestBody OrderMenuSettingsRequest request) {
    LocalDate operationDateFromNow = OperationDateUtils.getOperationDateFromNow();
    return ApiResponse.ok(
        orderMenuSettingsApiService.create(shopId, request.toServiceRequest(), operationDateFromNow)
    );
  }

  /**
   * 설정 - 메뉴 단건 수정 (모달)
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/update")
  public ApiResponse<OrderMenuSettingsResponse> updateMenu(@PathVariable String shopId,
      @PathVariable String menuId, @Valid @RequestBody OrderMenuSettingsRequest request) {
    LocalDate operationDateFromNow = OperationDateUtils.getOperationDateFromNow();
    return ApiResponse.ok(
        orderMenuSettingsApiService.update(menuId, request.toServiceRequest(), operationDateFromNow)
    );
  }

  /**
   * 설정 - 메뉴 단건 삭제
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders/menus/{menuId}/delete")
  public ApiResponse<?> deleteMenu(@PathVariable String shopId, @PathVariable String menuId) {
    orderMenuSettingsApiService.delete(menuId);
    return ApiResponse.ok();
  }

}
