package co.wadcorp.waiting.api.internal.service.shop;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.stream.StreamUtils;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.RemoteShopBulkServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopBulkResponse;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopBulkResponse.ShopSeqPair;
import co.wadcorp.waiting.api.service.channel.ChannelService;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.query.shop.ShopQueryRepository;
import co.wadcorp.waiting.data.query.shop.dto.ShopSeqShopIdDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RemoteShopBulkApiService {

  private final ChannelService channelService;
  private final ShopQueryRepository shopQueryRepository;

  /**
   * asc 순서 보장이 필요하다.
   */
  public RemoteShopBulkResponse findShopsByBulk(String channelId,
      RemoteShopBulkServiceRequest request) {
    List<ShopSeqShopIdDto> shopSeqShopIdDtos = shopQueryRepository.findShopSeqsByNoOffsetPaging(
        request.getMinSeq(), request.getSize());

    Map<String, String> channelIdMap = createShopIdChannelIdMap(channelId, shopSeqShopIdDtos);

    return RemoteShopBulkResponse.builder()
        .shopIdPairs(createShopIdPairs(shopSeqShopIdDtos, channelIdMap))
        .build();
  }

  private Map<String, String> createShopIdChannelIdMap(String channelId,
      List<ShopSeqShopIdDto> shopSeqShopIdDtos) {
    List<String> shopIds = convert(shopSeqShopIdDtos, ShopSeqShopIdDto::getShopId);
    List<ChannelMappingEntity> channelMappings = channelService.getChannelMappingByWaitingShopIds(
        channelId, shopIds);

    return channelMappings.stream()
        .collect(Collectors.toMap(ChannelMappingEntity::getShopId,
            ChannelMappingEntity::getChannelShopId));
  }

  private List<ShopSeqPair> createShopIdPairs(List<ShopSeqShopIdDto> seqShopIdDtos,
      Map<String, String> channelIdMap) {
    return seqShopIdDtos.stream()
        .map(seqShopIdDto -> {
          String channelShopId = channelIdMap.getOrDefault(seqShopIdDto.getShopId(), null);
          return createShopSeqPair(seqShopIdDto, channelShopId);
        })
        .toList();
  }

  /**
   * seqShopIdDto.seq : ShopEntity의 Seq
   * <p>
   * seqShopIdDto.shopId : ShopEntity의 ShopId (UUID)
   * <p>
   * channelShopId : 매핑 채널의 shop seq
   */
  private ShopSeqPair createShopSeqPair(ShopSeqShopIdDto seqShopIdDto, String channelShopId) {
    return ShopSeqPair.builder()
        .seq(seqShopIdDto.getSeq())
        .shopId(channelShopId != null ? Long.valueOf(channelShopId) : null)
        .waitingShopId(seqShopIdDto.getShopId())
        .build();
  }

}
