package co.wadcorp.waiting.api.internal.controller.shop;

import co.wadcorp.waiting.api.internal.controller.shop.dto.request.RemoteShopBulkRequest;
import co.wadcorp.waiting.api.internal.controller.shop.dto.request.RemoteShopOperationRequest;
import co.wadcorp.waiting.api.internal.service.shop.RemoteShopApiService;
import co.wadcorp.waiting.api.internal.service.shop.RemoteShopBulkApiService;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopBulkResponse;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RemoteShopController {

  private final RemoteShopApiService remoteShopApiService;
  private final RemoteShopBulkApiService remoteShopBulkApiService;

  /**
   * 원격 웨이팅 매장 정보 목록
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}")
  public ApiResponse<List<RemoteShopOperationResponse>> findShopOperations(
      @ShopId ChannelShopIdMapping shopIdMapping, @Valid RemoteShopOperationRequest request) {
    ZonedDateTime now = ZonedDateTimeUtils.nowOfSeoul();

    return ApiResponse.ok(
        remoteShopApiService.findShopOperations(shopIdMapping, request.toServiceRequest(), now)
    );
  }

  /**
   * 원격 웨이팅 매장 bulk
   */
  @GetMapping(value = "/internal/api/v1/shops/bulk")
  public ApiResponse<RemoteShopBulkResponse> findOperationInfoByBulk(
      @RequestHeader("X-CHANNEL-ID") String channelId,
      @Valid RemoteShopBulkRequest request) {
    return ApiResponse.ok(remoteShopBulkApiService.findShopsByBulk(channelId, request.toServiceRequest()));
  }

}
