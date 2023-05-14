package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategorySettingListRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategorySettingsRequest;
import co.wadcorp.waiting.api.service.settings.OrderCategorySettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsListResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategorySettingsResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderCategorySettingsController {

  private final OrderCategorySettingsApiService orderSettingsApiService;

  /**
   * 설정 - 단건 카테고리 생성
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders/categories")
  public ApiResponse<OrderCategorySettingsResponse> saveOrderCategory(
      @PathVariable String shopId,
      @Valid @RequestBody OrderCategorySettingsRequest request
  ) {
    return ApiResponse.ok(orderSettingsApiService.saveCategory(shopId, request.toServiceRequest()));
  }

  /**
   * 설정 - 카테고리 리스트 조회
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/orders/categories")
  public ApiResponse<OrderCategorySettingsListResponse> getOrderCategories(
      @PathVariable String shopId) {

    return ApiResponse.ok(orderSettingsApiService.getCategories(shopId));
  }

  /**
   * 설정 - 카테고리 리스트 저장
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders/categories/list")
  public ApiResponse<OrderCategorySettingsListResponse> saveAllCategories(
      @PathVariable String shopId,
      @Valid @RequestBody OrderCategorySettingListRequest request
  ) {
    return ApiResponse.ok(
        orderSettingsApiService.saveAllCategories(shopId, request.toServiceRequest()));
  }

}
