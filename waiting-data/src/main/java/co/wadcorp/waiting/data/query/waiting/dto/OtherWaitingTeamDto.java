package co.wadcorp.waiting.data.query.waiting.dto;

import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class OtherWaitingTeamDto {

  /**
   * waiting info
  * */
  private final Long seq;
  private final String seatOptionName;
  private final ZonedDateTime regDateTime;

  /**
   * shop info
   * */
  private final String shopId;
  private final String shopName;
  private final HomeSettingsData homeSettingsData;

  @QueryProjection
  public OtherWaitingTeamDto(Long seq, String seatOptionName, ZonedDateTime regDateTime, String shopId, String shopName,
      HomeSettingsData homeSettingsData) {
    this.seq = seq;
    this.seatOptionName = seatOptionName;
    this.regDateTime = regDateTime;
    this.shopId = shopId;
    this.shopName = shopName;
    this.homeSettingsData = homeSettingsData;
  }
}
