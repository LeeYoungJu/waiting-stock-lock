package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.query.waiting.dto.MyWaitingInfoDto;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
public class MyWaitingInfoResponse {

  private final Integer waitingNumber;
  private final String lastPhoneNumber;
  private final Integer totalPersonCount;
  private final String regDateTime;

  public static MyWaitingInfoResponse toDto(MyWaitingInfoDto waiting) {
    String phoneStr = waiting.getPhoneNumber().getLocal();
    return MyWaitingInfoResponse.builder()
        .waitingNumber(waiting.getWaitingNumber())
        .lastPhoneNumber(StringUtils.substring(phoneStr, phoneStr.length() - 4))
        .totalPersonCount(waiting.getTotalPersonCount())
        .regDateTime(ISO8601.format(waiting.getRegDateTime()))
        .build();
  }
}
