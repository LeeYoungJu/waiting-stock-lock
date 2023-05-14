package co.wadcorp.waiting.api.controller.settings;

import co.wadcorp.waiting.api.controller.settings.dto.request.OrderSettingsRequest;
import co.wadcorp.waiting.api.service.settings.OrderSettingsApiService;
import co.wadcorp.waiting.api.service.settings.dto.response.OrderSettingsResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderSettingsController {

  private final OrderSettingsApiService orderSettingsApiService;

  /**
   * 설정 - 주문 설정 조회
   */
  @GetMapping("/api/v1/shops/{shopId}/settings/orders")
  public ApiResponse<OrderSettingsResponse> getOrderSetting(@PathVariable String shopId) {

    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowZonedDateTime = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(
        orderSettingsApiService.getOrderSettings(shopId, operationDate, nowZonedDateTime)
    );
  }

  /**
   * 설정 - 주문 설정 저장
   */
  @PostMapping("/api/v1/shops/{shopId}/settings/orders")
  public ApiResponse<OrderSettingsResponse> saveOrderSetting(
      @PathVariable String shopId,
      @Valid @RequestBody OrderSettingsRequest request
  ) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    ZonedDateTime nowZonedDateTime = ZonedDateTimeUtils.nowOfSeoul();
    return ApiResponse.ok(
        orderSettingsApiService.saveOrderSettings(
            shopId, request.toServiceRequest(), operationDate, nowZonedDateTime
        )
    );
  }
}
