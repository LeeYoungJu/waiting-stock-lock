package co.wadcorp.waiting.api.internal.controller.person;

import co.wadcorp.waiting.api.internal.service.person.RemotePersonOptionApiService;
import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.data.api.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RemotePersonOptionController {

  private final RemotePersonOptionApiService remotePersonOptionApiService;

  /**
   * 원격 웨이팅 인원 옵션 목록
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}/person-options")
  public ApiResponse<List<RemotePersonOptionResponse>> findPersonOptions(
      @ShopId ChannelShopIdMapping channelShopIdMapping) {
    return ApiResponse.ok(remotePersonOptionApiService.findPersonOptions(channelShopIdMapping));
  }

}
