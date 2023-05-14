package co.wadcorp.waiting.api.model.channel.requet;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 채널링 매장을 등록/변경하는데 사용하는 요청 객체.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChannelMappingRequest implements Serializable {

  /**
   * 채널링 매장 ID. (예: shopSeq)
   */
  private String channelShopId;

  /**
   * 채널링 매장 연결 여부. 이 값을 false로 전달하면 채널링 매장과의 연결을 끊는다.
   */
  @JsonProperty("isConnected")
  private boolean isConnected;

}
