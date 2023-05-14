package co.wadcorp.waiting.api.controller.waiting.management;

import co.wadcorp.waiting.api.model.waiting.response.ShopOperationInitializerResponse;
import co.wadcorp.waiting.api.service.waiting.ShopOperationInitializerApiService;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ManagementShopOperationInitializerController {

  private final ShopOperationInitializerApiService apiService;

  /**
   * 웨이팅 운영일 기준 초기 데이터 생성 API
   *
   * @param shopId
   * @return
   */
  @PostMapping("/api/v1/shops/{shopId}/management/operation/init")
  public ApiResponse<ShopOperationInitializerResponse> initialize(
      @PathVariable String shopId,
      @RequestHeader("X-CTM-AUTH") String ctmAuth) {
    return ApiResponse.ok(
        apiService.initializer(
            shopId,
            OperationDateUtils.getOperationDateFromNow(),
            ZonedDateTimeUtils.nowOfSeoul()
        )
    );
  }

}
