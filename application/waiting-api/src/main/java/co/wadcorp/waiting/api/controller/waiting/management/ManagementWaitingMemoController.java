package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.controller.waiting.management.dto.request.WaitingMemoSaveRequest;
import co.wadcorp.waiting.api.service.waiting.management.ManagementWaitingMemoApiService;
import co.wadcorp.waiting.api.service.waiting.management.dto.response.WaitingMemoSaveResponse;
import co.wadcorp.waiting.data.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ManagementWaitingMemoController {

  private final ManagementWaitingMemoApiService managementWaitingMemoApiService;

  @PostMapping("/api/v1/shops/{shopId}/management/waiting/{waitingId}/memo")
  public ApiResponse<WaitingMemoSaveResponse> saveWaitingMemo(@PathVariable String shopId,
      @PathVariable String waitingId, @Valid @RequestBody WaitingMemoSaveRequest request) {
    return ApiResponse.ok(
        managementWaitingMemoApiService.save(shopId, request.toServiceRequest())
    );
  }

  @PostMapping("/api/v1/shops/{shopId}/management/waiting/{waitingId}/memo/delete")
  public ApiResponse<?> deleteWaitingMemo(@PathVariable String shopId,
      @PathVariable String waitingId) {
    managementWaitingMemoApiService.delete(waitingId);
    return ApiResponse.ok();
  }

}
