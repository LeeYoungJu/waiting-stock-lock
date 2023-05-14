package co.wadcorp.waiting.data.service.channel;

import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.infra.channel.JpaChannelMappingRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채널링 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelectChannelService {

  private final JpaChannelMappingRepository jpaChannelMappingRepository;


  @Transactional(readOnly = true)
  public Optional<ChannelMappingEntity> getChannelMappingByWaitingShopIds(String channelId,
      String shopId) {
    return jpaChannelMappingRepository.findByWaitingShopId(channelId, shopId);
  }

  @Transactional(readOnly = true)
  public List<ChannelMappingEntity> getChannelMappingByWaitingShopIds(String channelId,
      List<String> channelShopIds) {
    return jpaChannelMappingRepository.findByWaitingShopIds(channelId, channelShopIds);
  }

}
