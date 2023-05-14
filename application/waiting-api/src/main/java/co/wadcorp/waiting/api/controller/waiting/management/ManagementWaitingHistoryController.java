package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.model.waiting.response.WaitingHistoriesResponse;
import co.wadcorp.waiting.api.service.waiting.WaitingHistoryApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ManagementWaitingHistoryController {

  private final WaitingHistoryApiService apiService;

  @GetMapping("/api/v1/shops/{shopId}/management/waiting/{waitingId}/histories")
  public ApiResponse<WaitingHistoriesResponse> getWaitingHistory(@PathVariable String shopId, @PathVariable String waitingId) {
    return ApiResponse.ok(apiService.getWaitingHistories(shopId, waitingId));
  }
}
