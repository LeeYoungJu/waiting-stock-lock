package co.wadcorp.waiting.data.query.waiting.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WaitingCurrentStatusCountDto {

  private Long seq;
  private String shopId;
  private int totalPersonCount;
  private String seatOptionName;

  public WaitingCurrentStatusCountDto(Long seq, String shopId, int totalPersonCount, String seatOptionName) {
    this.seq = seq;
    this.shopId = shopId;
    this.totalPersonCount = totalPersonCount;
    this.seatOptionName = seatOptionName;
  }

  public static WaitingCurrentStatusCountDto createDefault(String shopId) {
    return new WaitingCurrentStatusCountDto(null, shopId, 0, "");
  }

}
