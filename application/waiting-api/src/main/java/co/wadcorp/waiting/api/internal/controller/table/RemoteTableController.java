package co.wadcorp.waiting.api.internal.controller.table;

import co.wadcorp.waiting.api.internal.service.table.RemoteTableApiService;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.data.api.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RemoteTableController {

  private final RemoteTableApiService remoteTableApiService;

  /**
   * 원격 웨이팅 테이블 정보 목록
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}/tables")
  public ApiResponse<List<RemoteTableSettingResponse>> findTableSettings(
      @ShopId ChannelShopIdMapping channelShopIdMapping) {
    return ApiResponse.ok(remoteTableApiService.findTableSettings(channelShopIdMapping));
  }

}
