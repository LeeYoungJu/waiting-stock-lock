package co.wadcorp.waiting.data.support;

import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseHistoryEntity {

  @Column(name = "reg_date_time", updatable = false)
  private ZonedDateTime regDateTime;

  @PrePersist
  public void prePersist() {
    this.regDateTime = ZonedDateTimeUtils.nowOfSeoul();
  }

}
