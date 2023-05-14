package co.wadcorp.waiting.api.controller.waiting.register;

import co.wadcorp.waiting.api.controller.waiting.register.dto.request.RegisterWaitingOrderMenuRequest;
import co.wadcorp.waiting.api.controller.waiting.register.dto.request.ValidateWaitingOrderManuStockRequest;
import co.wadcorp.waiting.api.service.waiting.register.RegisterDisplayMenuApiService;
import co.wadcorp.waiting.api.service.waiting.register.RegisterValidateMenuApiService;
import co.wadcorp.waiting.api.service.waiting.register.dto.response.RegisterWaitingOrderMenuResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RegisterWaitingOrderController {

  private final RegisterDisplayMenuApiService registerDisplayMenuApiService;
  private final RegisterValidateMenuApiService registerValidateMenuApiService;

  /**
   * 등록 - 웨이팅 등록 시 카테고리/메뉴 조회
   *
   * @param shopId
   * @param request
   * @return
   */
  @GetMapping("/api/v1/shops/{shopId}/register/orders")
  public ApiResponse<RegisterWaitingOrderMenuResponse> getOrderMenu(@PathVariable String shopId,
      @Valid RegisterWaitingOrderMenuRequest request) {

    return ApiResponse.ok(
        registerDisplayMenuApiService.getOrderMenu(
            shopId,
            request.getDisplayMappingType(),
            OperationDateUtils.getOperationDateFromNow()
        )
    );
  }

  /**
   * 등록 - 웨이팅 등록 시 재고 체크
   *
   * @param shopId
   * @param request
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/register/orders/stock-validation")
  public ApiResponse<?> validateStock(@PathVariable String shopId,
      @RequestBody ValidateWaitingOrderManuStockRequest request) {

    registerValidateMenuApiService.validateStock(
        shopId,
        OperationDateUtils.getOperationDateFromNow(),
        request.toServiceRequest()
    );
    return ApiResponse.ok();
  }

}
