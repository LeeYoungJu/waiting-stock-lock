package co.wadcorp.waiting.api.internal.controller.meta;

import co.wadcorp.waiting.api.internal.service.meta.RemoteMetaApiService;
import co.wadcorp.waiting.api.internal.service.meta.dto.RemoteMetaResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RemoteMetaController {


  private final RemoteMetaApiService remoteMetaApiService;

  /**
   * 원격 웨이팅 메타 정보 목록
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}/meta")
  public ApiResponse<List<RemoteMetaResponse>> findMeta(
      @ShopId ChannelShopIdMapping channelShopIdMapping) {

    return ApiResponse.ok(remoteMetaApiService.getMeta(
        channelShopIdMapping,
        OperationDateUtils.getOperationDateFromNow(),
        ZonedDateTimeUtils.nowOfSeoul()
    ));
  }


}
