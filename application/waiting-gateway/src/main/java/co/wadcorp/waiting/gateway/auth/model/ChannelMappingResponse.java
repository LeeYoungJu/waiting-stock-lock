package co.wadcorp.waiting.gateway.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 채널링 매장 응답 객체. 내부 호출에서만 사용한다.
 */
@Getter
@AllArgsConstructor
@ToString
public class ChannelMappingResponse {

  private String channelId;
  private String shopId;
  private String channelShopId;
  @JsonProperty("isConnected")
  private boolean isConnected;

}
