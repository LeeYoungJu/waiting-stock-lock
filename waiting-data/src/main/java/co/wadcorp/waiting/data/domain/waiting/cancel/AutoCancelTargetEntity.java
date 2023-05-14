package co.wadcorp.waiting.data.domain.waiting.cancel;

import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.CREATED;
import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.IGNORE;
import static co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelProcessingStatus.SUCCESS;

import co.wadcorp.waiting.data.support.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cw_auto_cancel_target")
public class AutoCancelTargetEntity extends BaseEntity {

  public static AutoCancelTargetEntity EMPTY = new AutoCancelTargetEntity();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "waiting_id")
  private String waitingId;

  @Column(name = "expected_cancel_date_time")
  private ZonedDateTime expectedCancelDateTime;

  @Column(name = "processing_status")
  @Enumerated(EnumType.STRING)
  private AutoCancelProcessingStatus processingStatus;

  @Builder
  private AutoCancelTargetEntity(String shopId, String waitingId,
      ZonedDateTime expectedCancelDateTime,
      AutoCancelProcessingStatus processingStatus) {
    this.shopId = shopId;
    this.waitingId = waitingId;
    this.expectedCancelDateTime = expectedCancelDateTime;
    this.processingStatus = processingStatus;
  }

  public static AutoCancelTargetEntity init(String shopId, String waitingId,
      ZonedDateTime expectedCancelDateTime) {
    return AutoCancelTargetEntity.builder()
        .shopId(shopId)
        .waitingId(waitingId)
        .expectedCancelDateTime(expectedCancelDateTime)
        .processingStatus(CREATED)
        .build();
  }

  public void success() {
    if (this.processingStatus.isCompletedStatus()) {
      return;
    }
    this.processingStatus = SUCCESS;
  }

  public void ignore() {
    if (this.processingStatus.isCompletedStatus()) {
      return;
    }
    this.processingStatus = IGNORE;
  }

}
