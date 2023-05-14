package co.wadcorp.waiting.api.controller.channel;

import co.wadcorp.waiting.api.model.channel.requet.ChannelMappingRequest;
import co.wadcorp.waiting.api.model.channel.response.ChannelMappingResponse;
import co.wadcorp.waiting.api.service.channel.ChannelService;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.infra.pos.CatchtablePosShopClient;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채널링 관련 API
 *
 * <p>이 컨트롤러에서는 shopId 대신에 waitingShopId를 사용합니다. 다른 곳에서 shopId를 사용하면서 shopId에 대해 일관적으로 처리하는
 * 부분이 있는데, 그런 부분과 구분하기 위해 waitingShopId로 사용합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final CatchtablePosShopClient catchtablePosShopClient;

  /**
   * 채널링 등록 상태 조회 API.
   *
   * @param channelId                    채널 ID
   * @param waitingShopIdOrChannelShopId 웨이팅쪽의 shopId 혹은 채널의 shopId(channelShopId)
   * @param isChannelShopId              waitingShopIdOrChannelShopId가 channelShopId인지 여부
   */
  @GetMapping("/api/v1/channels/{channelId}/shops/{waitingShopIdOrChannelShopId}/mappings")
  public ChannelMappingResponse getChannelMapping(
      @PathVariable("channelId") String channelId,
      @PathVariable("waitingShopIdOrChannelShopId") String waitingShopIdOrChannelShopId,
      @RequestParam(value = "isChannelShopId", required = false, defaultValue = "false") boolean isChannelShopId
  ) {
    Optional<ChannelMappingEntity> optChannelMapping;
    if (isChannelShopId) {
      optChannelMapping = channelService.getChannelMappingByChannelShopId(channelId,
          waitingShopIdOrChannelShopId);
    } else {
      optChannelMapping = channelService.getChannelMapping(channelId, waitingShopIdOrChannelShopId);
    }
    if (optChannelMapping.isEmpty()) {
      throw new AppException(HttpStatus.CONFLICT, "채널링 매핑 정보가 없습니다.");
    }

    ChannelMappingEntity entity = optChannelMapping.get();
    return ChannelMappingResponse.of(entity);
  }

  /**
   * 채널링 변경 API. 웨이팅쪽의 shopId와 채널링쪽의 ID(예:shopSeq)의 매핑 상태를 변경한다.
   *
   * @param shopId 웨이팅쪽의 shopId
   */
  @PatchMapping("/api/v1/channels/{channelId}/shops/{waitingShopId}/mappings")
  public ChannelMappingResponse updateChannelMapping(@PathVariable("channelId") String channelId,
      @PathVariable("waitingShopId") String shopId, @RequestBody ChannelMappingRequest request) {

    log.info("channel mapping request. channelId:{}, waitingShopId:{}, request:{}", channelId,
        shopId, request);

    // shopId validation
    var shopResponse = catchtablePosShopClient.getShopForInternal(shopId);
    if (shopResponse.isError()) {
      // TODO: PosShopResponse 에서 Exception 처리에 대해 조금 더 보강이 이뤄져야 한다.
      throw new AppException(HttpStatus.CONFLICT, "invalid shopId");
    }
    // TODO: 추후 추가 validation 필요. (유효매장, 매장에 대기가 활성화되었는지 여부 등)

    var savedEntity = channelService.saveChannelMapping(channelId, shopId, request);

    return ChannelMappingResponse.of(savedEntity);
  }
}
