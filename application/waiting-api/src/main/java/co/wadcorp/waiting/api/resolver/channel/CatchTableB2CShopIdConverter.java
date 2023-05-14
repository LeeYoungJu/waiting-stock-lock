package co.wadcorp.waiting.api.resolver.channel;

import co.wadcorp.waiting.api.service.channel.ChannelService;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import com.github.benmanes.caffeine.cache.Cache;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 캐치테이블 B2C의 shopSeq를 ChannelShopIdMapping으로 변환한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatchTableB2CShopIdConverter implements ChannelShopIdConverter {

  // key: channelShopId, value: shopId
  private final Cache<String, String> b2cChannelShopIdCache;
  private final ChannelService channelService;

  public boolean isSupport(ServiceChannelId serviceChannelId) {
    return ServiceChannelId.CATCHTABLE_B2C.equals(serviceChannelId);
  }

  @Override
  public ChannelShopIdMapping getShopIds(List<String> channelShopIds) {
    Map<String, String> mapping = b2cChannelShopIdCache.getAll(
        channelShopIds, s -> {
          List<ChannelMappingEntity> channelMapping = channelService.getChannelMappingByChannelShopIds(
              ServiceChannelId.CATCHTABLE_B2C.getValue(),
              channelShopIds);
          if (channelMapping.isEmpty()) {
            throw new AppException(HttpStatus.CONFLICT, "channelShopId 조회 실패");
          }

         return channelMapping.stream()
             .collect(Collectors.toMap(ChannelMappingEntity::getChannelShopId, ChannelMappingEntity::getShopId));
        });

    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();
    mapping.keySet()
        .forEach(key -> channelShopIdMapping.put(mapping.get(key), key));

    return channelShopIdMapping;
  }
}
