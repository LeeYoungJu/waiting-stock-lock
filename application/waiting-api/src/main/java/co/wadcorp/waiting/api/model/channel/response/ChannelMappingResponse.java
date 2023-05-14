package co.wadcorp.waiting.api.model.channel.response;

import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 채널링 매장 응답 객체.
 */
@Getter
@AllArgsConstructor
public class ChannelMappingResponse {

  /**
   * ChannelMappingEntity로부터 ChannelMappingResponse를 생성한다. 차후 생성 요건이 복잡해지면 ChannelMapoingConverter로
   * 분리한다.
   *
   * @param entity channel mapping entity 인스턴스.
   */
  public static ChannelMappingResponse of(ChannelMappingEntity entity) {
    var channelMappingResponse = new ChannelMappingResponse(
        entity.getChannelId(),
        entity.getShopId(),
        entity.getChannelShopId(),
        entity.isConnected()
    );
    return channelMappingResponse;
  }

  private String channelId;
  private String shopId;
  private String channelShopId;
  @JsonProperty("isConnected")
  private boolean isConnected;

}
