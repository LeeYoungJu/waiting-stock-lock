package co.wadcorp.waiting.data.query.waiting.dto;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitingOnRegistrationDto {

  private Long seq;
  private int waitingNumber;
  private String seatOptionName;
  private int totalPersonCount;
  private ZonedDateTime regDateTime;

}