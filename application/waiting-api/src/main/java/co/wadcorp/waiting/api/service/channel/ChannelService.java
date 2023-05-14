package co.wadcorp.waiting.api.service.channel;

import co.wadcorp.waiting.api.model.channel.requet.ChannelMappingRequest;
import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.infra.channel.JpaChannelMappingRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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
public class ChannelService {

  private final JpaChannelMappingRepository jpaChannelMappingRepository;

  /**
   * 등록된 채널을 얻는다.
   *
   * @param channelId 채널 ID
   * @param shopId    웨이팅쪽의 shopId
   * @return 채널링 정보
   */
  @Transactional(readOnly = true)
  public Optional<ChannelMappingEntity> getChannelMapping(String channelId, String shopId) {
    var optEntity = jpaChannelMappingRepository.findByWaitingShopId(channelId, shopId);
    return optEntity;
  }

  /**
   * 등록된 채널을 얻는다.
   *
   * @param channelId     채널 ID
   * @param channelShopId 채낼의 매장ID
   * @return 채널링 정보
   */
  @Transactional(readOnly = true)
  public Optional<ChannelMappingEntity> getChannelMappingByChannelShopId(String channelId,
      String channelShopId) {
    return jpaChannelMappingRepository.findByChannelShopId(channelId, channelShopId);
  }

  /**
   * 채널링 등록 API. 웨이팅쪽의 shopId와 채널링쪽의 ID(예:shopSeq)의 매핑 상태를 변경한다.
   *
   * @param shopId 웨이팅쪽의 shopId
   */
  @Transactional
  public ChannelMappingEntity saveChannelMapping(String channelId, String shopId,
      ChannelMappingRequest request) {
    final String channelShopId = request.getChannelShopId();

    AtomicReference<ChannelMappingEntity> arSaved = new AtomicReference<>();
    jpaChannelMappingRepository.findByChannelShopId(channelId, channelShopId)
        .ifPresentOrElse(exisingEntity -> {
          exisingEntity.changeConnected(request.isConnected());
          arSaved.set(jpaChannelMappingRepository.save(exisingEntity));
        }, () -> {
          var builder = ChannelMappingEntity.builder();
          var newChannelMappingEntity = builder.channelId(channelId).channelShopId(channelShopId)
              .shopId(shopId).isConnected(request.isConnected()).build();
          arSaved.set(jpaChannelMappingRepository.save(newChannelMappingEntity));
        });

    ChannelMappingEntity saved = arSaved.get();
    log.info("channel mapping saved. channelMapping:{}", saved.toString());
    return saved;
  }

  @Transactional(readOnly = true)
  public List<ChannelMappingEntity> getChannelMappingByChannelShopIds(String channelId,
      List<String> channelShopIds) {
    return jpaChannelMappingRepository.findByChannelShopIds(channelId, channelShopIds);
  }

  @Transactional(readOnly = true)
  public List<ChannelMappingEntity> getChannelMappingByWaitingShopIds(String channelId,
      List<String> shopIds) {
    return jpaChannelMappingRepository.findByWaitingShopIds(channelId, shopIds);
  }

}
