package co.wadcorp.waiting.data.query.waiting.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingOfOtherShopQueryDto {

  private String waitingId;
  private String shopId;
  private String shopName;
  private Long customerSeq;
  private Integer waitingOrder;
  private String seatOptionName;
  private ZonedDateTime regDateTime;

  public WaitingOfOtherShopQueryDto() {
  }

  public WaitingOfOtherShopQueryDto(String waitingId, String shopId, String shopName,
      Long customerSeq, Integer waitingOrder, String seatOptionName, ZonedDateTime regDateTime) {
    this.waitingId = waitingId;
    this.shopId = shopId;
    this.shopName = shopName;
    this.customerSeq = customerSeq;
    this.waitingOrder = waitingOrder;
    this.seatOptionName = seatOptionName;
    this.regDateTime = regDateTime;
  }
}
