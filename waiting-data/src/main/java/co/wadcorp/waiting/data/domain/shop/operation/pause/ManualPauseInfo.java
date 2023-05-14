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
public class ManualPauseInfo {

  @Column(name = "manual_pause_start_date_time")
  private ZonedDateTime manualPauseStartDateTime;

  @Column(name = "manual_pause_end_date_time")
  private ZonedDateTime manualPauseEndDateTime;

  @Column(name = "manual_pause_reason_id")
  private String manualPauseReasonId;

  @Column(name = "manual_pause_reason")
  private String manualPauseReason;

  public ManualPauseInfo() {
  }

  @Builder
  private ManualPauseInfo(ZonedDateTime manualPauseStartDateTime,
      ZonedDateTime manualPauseEndDateTime,
      String manualPauseReasonId, String manualPauseReason) {
    this.manualPauseStartDateTime = manualPauseStartDateTime;
    this.manualPauseEndDateTime = manualPauseEndDateTime;
    this.manualPauseReasonId = manualPauseReasonId;
    this.manualPauseReason = manualPauseReason;
  }
}
