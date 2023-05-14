package co.wadcorp.waiting.shared.enums;

import java.util.Arrays;
import lombok.Getter;

/**
 * 웨이팅 서비스를 사용하는 채널을 정의한다. value 값은 반드시 대문자로만 운용한다.
 */
@Getter
public enum ServiceChannelId {

  CATCH_WAITING("CATCH-WAITING"),
  CATCHTABLE_B2C("CATCHTABLE-B2C");

  private final String value;

  ServiceChannelId(String value) {
    this.value = value;
  }

  public static ServiceChannelId find(String channelId) {
    return Arrays.stream(ServiceChannelId.values())
        .filter(item -> item.getValue().equals(channelId))
        .findFirst()
        .orElseThrow();
  }
}
