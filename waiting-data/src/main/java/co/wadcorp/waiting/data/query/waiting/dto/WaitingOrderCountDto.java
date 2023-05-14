package co.wadcorp.waiting.data.query.waiting.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitingOrderCountDto {

  private String shopId;
  private Integer waitingOrder;
  private String seatOptionName;

}
