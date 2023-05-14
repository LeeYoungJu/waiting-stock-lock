package co.wadcorp.waiting.data.support;

import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseEntity {

  @Column(name = "reg_date_time", updatable = false)
  private ZonedDateTime regDateTime;

  @Column(name = "mod_date_time")
  private ZonedDateTime modDateTime;

  @PrePersist
  public void prePersist() {
    ZonedDateTime now = ZonedDateTimeUtils.nowOfSeoul()
        .truncatedTo(ChronoUnit.MICROS);

    this.regDateTime = now;
    this.modDateTime = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.modDateTime = ZonedDateTimeUtils.nowOfSeoul()
        .truncatedTo(ChronoUnit.MICROS);
  }

}
