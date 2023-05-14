package co.wadcorp.waiting.api.resolver.channel;

import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 캐치포스/웨이팅의 shopId를 처리한다. 웨이팅의 shopId는 캐치포스의 shopId와 동일하므로 bypass와 다름없다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatchPosShopIdConverter implements ChannelShopIdConverter {

  @Override
  public boolean isSupport(ServiceChannelId serviceChannelId) {
    return ServiceChannelId.CATCH_WAITING.equals(serviceChannelId);
  }

  @Override
  public ChannelShopIdMapping getShopIds(List<String> channelShopId) {
    ChannelShopIdMapping channelShopIdMapping = new ChannelShopIdMapping();

    channelShopId.forEach(item -> channelShopIdMapping.put(item, item));

    return channelShopIdMapping;
  }
}
