package co.wadcorp.waiting.data.domain.shop.operation.pause;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class RemoteAutoPauseInfo {

  @Column(name = "remote_auto_pause_start_date_time")
  private ZonedDateTime remoteAutoPauseStartDateTime;

  @Column(name = "remote_auto_pause_end_date_time")
  private ZonedDateTime remoteAutoPauseEndDateTime;

  @Builder
  private RemoteAutoPauseInfo(ZonedDateTime remoteAutoPauseStartDateTime,
      ZonedDateTime remoteAutoPauseEndDateTime) {
    this.remoteAutoPauseStartDateTime = remoteAutoPauseStartDateTime;
    this.remoteAutoPauseEndDateTime = remoteAutoPauseEndDateTime;
  }

}
