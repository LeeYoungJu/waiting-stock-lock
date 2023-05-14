package co.wadcorp.waiting.api.resolver.channel;

import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import java.util.List;

/**
 * 채널로부터 들어온 channelShopId를 ChannelShopIdMapping으로 변환한다.
 */
public interface ChannelShopIdConverter {

  boolean isSupport(ServiceChannelId serviceChannelId);

  ChannelShopIdMapping getShopIds(List<String> channelShopId);

}
