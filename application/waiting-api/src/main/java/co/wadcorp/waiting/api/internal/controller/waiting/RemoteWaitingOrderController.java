package co.wadcorp.waiting.api.internal.controller.waiting;

import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingOrderMenuRequest;
import co.wadcorp.waiting.api.internal.controller.waiting.dto.request.RemoteWaitingOrderValidateRequest;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteDisplayMenuApiService;
import co.wadcorp.waiting.api.internal.service.waiting.RemoteWaitingOrderValidateApiService;
import co.wadcorp.waiting.api.internal.service.waiting.dto.response.RemoteWaitingOrderMenuResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RemoteWaitingOrderController {

  private final RemoteDisplayMenuApiService remoteDisplayMenuApiService;
  private final RemoteWaitingOrderValidateApiService remoteWaitingOrderValidateApiService;

  /**
   * 원격 웨이팅 선주문 카테고리/메뉴 정보 조회
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}/orders")
  public ApiResponse<RemoteWaitingOrderMenuResponse> getOrderMenu(
      @ShopId ChannelShopIdMapping channelShopIdMapping,
      @Valid RemoteWaitingOrderMenuRequest request) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    return ApiResponse.ok(
        remoteDisplayMenuApiService.getOrderMenu(channelShopIdMapping,
            request.getDisplayMappingType(), operationDate)
    );
  }

  /**
   * 원격 웨이팅 선주문 카테고리/메뉴 정보 검증
   */
  @PostMapping(value = "/internal/api/v1/shops/{shopIds}/orders/validation")
  public ApiResponse<?> checkOrderMenu(@ShopId ChannelShopIdMapping channelShopIdMapping,
      @Valid @RequestBody RemoteWaitingOrderValidateRequest request) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    remoteWaitingOrderValidateApiService.validateOrderMenus(
        channelShopIdMapping,
        operationDate,
        request.toServiceRequest()
    );

    return ApiResponse.ok();
  }
}
