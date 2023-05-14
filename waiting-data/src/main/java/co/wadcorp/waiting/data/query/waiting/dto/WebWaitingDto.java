package co.wadcorp.waiting.data.query.waiting.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebWaitingDto {

  private String waitingId;
  private String shopId;
  private String shopName;
  private Integer waitingOrder;
  private String seatOptionName;
  private ZonedDateTime regDateTime;

  @Builder
  private WebWaitingDto(String waitingId, String shopId, String shopName, Integer waitingOrder,
      String seatOptionName,
      ZonedDateTime regDateTime) {
    this.waitingId = waitingId;
    this.shopId = shopId;
    this.shopName = shopName;
    this.waitingOrder = waitingOrder;
    this.seatOptionName = seatOptionName;
    this.regDateTime = regDateTime;
  }

}
