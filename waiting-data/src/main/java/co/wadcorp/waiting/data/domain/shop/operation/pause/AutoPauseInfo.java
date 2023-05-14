package co.wadcorp.waiting.data.domain.shop.operation.pause;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class AutoPauseInfo {

  @Column(name = "auto_pause_start_date_time")
  private ZonedDateTime autoPauseStartDateTime;

  @Column(name = "auto_pause_end_date_time")
  private ZonedDateTime autoPauseEndDateTime;

  @Column(name = "auto_pause_reason_id")
  private String autoPauseReasonId;

  @Column(name = "auto_pause_reason")
  private String autoPauseReason;

  public AutoPauseInfo() {
  }

  @Builder
  private AutoPauseInfo(ZonedDateTime autoPauseStartDateTime, ZonedDateTime autoPauseEndDateTime,
      String autoPauseReasonId, String autoPauseReason) {
    this.autoPauseStartDateTime = autoPauseStartDateTime;
    this.autoPauseEndDateTime = autoPauseEndDateTime;
    this.autoPauseReasonId = autoPauseReasonId;
    this.autoPauseReason = autoPauseReason;
  }

  public boolean isBetweenAutoPauseRange(ZonedDateTime nowLocalDateTime) {
    if (nowLocalDateTime == null) {
      return false;
    }

    return !(nowLocalDateTime.isBefore(autoPauseStartDateTime)
        || nowLocalDateTime.isAfter(autoPauseEndDateTime));
  }

}
