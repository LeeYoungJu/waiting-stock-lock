package co.wadcorp.waiting.data.query.waiting.dto;

import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class WaitingCalledCountDto {

  public static final WaitingCalledCountDto EMPTY = new WaitingCalledCountDto(0, null);

  private final long callCount;
  private final ZonedDateTime calledDateTime;

  public WaitingCalledCountDto(long callCount, ZonedDateTime calledDateTime) {
    this.callCount = callCount;
    this.calledDateTime = calledDateTime;
  }
}
