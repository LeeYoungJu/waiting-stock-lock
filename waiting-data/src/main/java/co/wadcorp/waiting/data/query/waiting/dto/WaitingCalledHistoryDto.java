package co.wadcorp.waiting.data.query.waiting.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class WaitingCalledHistoryDto {

  private final String waitingId;
  private final ZonedDateTime regDateTime;

  @QueryProjection
  public WaitingCalledHistoryDto(String waitingId, ZonedDateTime regDateTime) {
    this.waitingId = waitingId;
    this.regDateTime = regDateTime;
  }
}
