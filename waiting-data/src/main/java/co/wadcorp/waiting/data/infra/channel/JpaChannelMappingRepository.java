package co.wadcorp.waiting.data.infra.channel;

import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChannelMappingRepository extends
    JpaRepository<ChannelMappingEntity, Long> {

  @Query("SELECT m FROM ChannelMappingEntity m WHERE m.channelId = :channelId AND m.channelShopId = :channelShopId")
  Optional<ChannelMappingEntity> findByChannelShopId(@Param("channelId") String channelId,
      @Param("channelShopId") String channelShopId);


  @Query("SELECT m FROM ChannelMappingEntity m WHERE m.channelId = :channelId AND m.channelShopId in :channelShopIds")
  List<ChannelMappingEntity> findByChannelShopIds(@Param("channelId") String channelId,
      @Param("channelShopIds") List<String> channelShopIds);

  @Query("SELECT m FROM ChannelMappingEntity m WHERE m.channelId = :channelId AND m.shopId = :shopId")
  Optional<ChannelMappingEntity> findByWaitingShopId(@Param("channelId") String channelId,
      @Param("shopId") String shopId);

  @Query("SELECT m FROM ChannelMappingEntity m WHERE m.channelId = :channelId AND m.shopId in :shopIds")
  List<ChannelMappingEntity> findByWaitingShopIds(@Param("channelId") String channelId,
      @Param("shopIds") List<String> shopIds);
}
