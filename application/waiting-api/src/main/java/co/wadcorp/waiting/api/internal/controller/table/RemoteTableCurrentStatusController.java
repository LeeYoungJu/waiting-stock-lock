package co.wadcorp.waiting.api.internal.controller.table;

import co.wadcorp.waiting.api.internal.service.table.RemoteTableCurrentStatusApiService;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableStatusResponse;
import co.wadcorp.waiting.api.resolver.channel.ChannelShopIdMapping;
import co.wadcorp.waiting.api.resolver.channel.ShopId;
import co.wadcorp.waiting.data.api.ApiResponse;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RemoteTableCurrentStatusController {

  private final RemoteTableCurrentStatusApiService remoteTableCurrentStatusApiService;

  /**
   * 원격 웨이팅 테이블 현황
   */
  @GetMapping(value = "/internal/api/v1/shops/{shopIds}/current-status")
  public ApiResponse<List<RemoteTableStatusResponse>> findTableCurrentStatus(
      @ShopId ChannelShopIdMapping channelShopIdMapping) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    return ApiResponse.ok(
        remoteTableCurrentStatusApiService.findTableCurrentStatus(channelShopIdMapping,
            operationDate)
    );
  }

}
