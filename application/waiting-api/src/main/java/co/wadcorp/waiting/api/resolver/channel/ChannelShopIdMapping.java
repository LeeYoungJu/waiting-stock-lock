package co.wadcorp.waiting.api.resolver.channel;

import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;


/**
 * mapping Key는 WaitingShopId, Value는 ChannelShopId
 */
public class ChannelShopIdMapping {

  private final Map<String, String> mapping = new HashMap<>();

  public ChannelShopIdMapping() {
  }

  public ChannelShopIdMapping(Map<String, String> mapping) {
    this.mapping.putAll(mapping);
  }

  public void put(String waitingShopId, String channelShopId) {
    this.mapping.put(waitingShopId, channelShopId);
  }

  public String getChannelShopId(String waitingShopId) {
    return this.mapping.getOrDefault(waitingShopId, "");
  }

  public String getFirstWaitingShopId() {
    return this.mapping.keySet()
        .stream().findFirst()
        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, ""));
  }

  public List<String> getAllWaitingShopIds() {
    return new ArrayList<>(mapping.keySet());
  }

  public void checkOnlyOneShopId() {
    Set<String> waitingShopIds = this.mapping.keySet();
    if (waitingShopIds.size() != 1) {
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.ONLY_ONE_SHOP_ID);
    }
  }
}
