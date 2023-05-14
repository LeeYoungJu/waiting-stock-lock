package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.controller.waiting.management.dto.request.ManagementStockUpdateRequest;
import co.wadcorp.waiting.api.service.waiting.management.ManagementStockApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.ManagementStockListResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ManagementStockController {

  private final ManagementStockApiService managementStockApiService;

  /**
   * 대시보드 - 재고 관리 모달 조회
   */
  @GetMapping("/api/v1/shops/{shopId}/management/stock")
  public ApiResponse<ManagementStockListResponse> getStocks(@PathVariable String shopId) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    return ApiResponse.ok(managementStockApiService.getStocks(shopId, operationDate));
  }

  /**
   * 대시보드 - 재고 관리 모달 저장
   */
  @PostMapping("/api/v1/shops/{shopId}/management/stock")
  public ApiResponse<ManagementStockListResponse> updateStocks(@PathVariable String shopId,
      @RequestHeader("X-REQUEST-ID") String deviceId,
      @Valid @RequestBody ManagementStockUpdateRequest request
  ) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    return ApiResponse.ok(
        managementStockApiService.updateStocks(shopId, request.toServiceRequest(), operationDate, deviceId)
    );
  }

}
