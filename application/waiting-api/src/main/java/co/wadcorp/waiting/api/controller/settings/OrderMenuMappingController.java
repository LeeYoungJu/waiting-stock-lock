package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryMappingSaveRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderCategoryOrderingSaveRequest;
import co.wadcorp.waiting.api.controller.settings.dto.request.OrderDisplayMenuRequest;
import co.wadcorp.waiting.api.service.settings.OrderMenuMappingApiService;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderCategoryOrderingServiceResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderDisplayMenuMappingResponse;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderMenuMappingServiceResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OrderMenuMappingController {

  private final OrderMenuMappingApiService orderMenuMappingApiService;

  /**
   * 설정 - 유형별 주문 설정 조회
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/orders/menu-mapping")
  public ApiResponse<OrderDisplayMenuMappingResponse> getDisplayMappingMenus(
      @PathVariable String shopId,
      @Valid OrderDisplayMenuRequest request) {

    return ApiResponse.ok(
        orderMenuMappingApiService.getDisplayMappingMenus(shopId, request.toServiceRequest())
    );
  }

  /**
   * 설정 - 유형별 주문 설정 카테고리별 메뉴 저장
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders/categories/{categoryId}/menu-mapping")
  public ApiResponse<OrderMenuMappingServiceResponse> saveMenus(
      @PathVariable String shopId, @PathVariable String categoryId,
      @Valid @RequestBody OrderCategoryMappingSaveRequest request) {

    return ApiResponse.ok(
        orderMenuMappingApiService.saveMenuMapping(categoryId, request.toServiceRequest())
    );
  }

  /**
   * 설정 - 유형별 주문 설정 카테고리 순서 저장 (모달)
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders/categories/ordering")
  public ApiResponse<OrderCategoryOrderingServiceResponse> saveCategoryOrdering(
      @PathVariable String shopId, @Valid @RequestBody OrderCategoryOrderingSaveRequest request) {

    return ApiResponse.ok(
        orderMenuMappingApiService.saveCategoryOrdering(shopId, request.toServiceRequest())
    );
  }

}
