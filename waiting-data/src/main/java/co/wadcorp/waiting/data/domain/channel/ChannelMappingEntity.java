package co.wadcorp.waiting.data.domain.channel;

import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 채널별로 웨이팅의 매장(shopId)과 채널의 매장(channelShopId)을 매핑한다.
 */
@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@ToString
@Table(name = "cw_channel_mapping")
public class ChannelMappingEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private long seq;

  @Column(name = "channel_id")
  private String channelId;

  @Column(name = "channel_shop_id")
  private String channelShopId;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "connected_yn", columnDefinition = "char(1), comment 'Y:연결됨, N:연결안됨'")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isConnected;

  public void changeConnected(boolean isConnected) {
    this.isConnected = isConnected;
  }

  /**
   * ChannelMappingEntity Builder.
   */
  @Builder
  public ChannelMappingEntity(String channelId, String channelShopId, String shopId,
      boolean isConnected) {
    this.channelId = channelId;
    this.channelShopId = channelShopId;
    this.shopId = shopId;
    this.isConnected = isConnected;
  }
}
