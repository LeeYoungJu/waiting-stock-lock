package co.wadcorp.waiting.data.domain.settings.remote;

import co.wadcorp.waiting.data.enums.OperationDay;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.data.support.BooleanYnConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 원격 매장 운영시간 설정 테이블 (앱에서는 제공하지 않는 기능)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "cw_remote_operation_time_settings",
    indexes = {
        @Index(name = "cw_remote_operation_time_settings_shop_id_index", columnList = "shop_id")
    }
)
@Entity
public class RemoteOperationTimeSettingsEntity extends BaseEntity {

  public static final RemoteOperationTimeSettingsEntity EMPTY = RemoteOperationTimeSettingsEntity.builder()
      .shopId("").build();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "seq")
  private Long seq;

  @Column(name = "shop_id")
  private String shopId;

  @Enumerated(EnumType.STRING)
  @Column(name = "operation_day")
  private OperationDay operationDay;

  @Column(name = "operation_start_time")
  private LocalTime operationStartTime;

  @Column(name = "operation_end_time")
  private LocalTime operationEndTime;

  @Column(name = "closed_day_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isClosedDay;

  @Column(name = "used_auto_pause_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private boolean isUsedAutoPause;

  @Column(name = "auto_pause_start_time")
  private LocalTime autoPauseStartTime;

  @Column(name = "auto_pause_end_time")
  private LocalTime autoPauseEndTime;

  @Column(name = "publish_yn", columnDefinition = "char")
  @Convert(converter = BooleanYnConverter.class)
  private Boolean isPublished;

  @Builder
  private RemoteOperationTimeSettingsEntity(String shopId, OperationDay operationDay,
      LocalTime operationStartTime, LocalTime operationEndTime, boolean isClosedDay,
      boolean isUsedAutoPause, LocalTime autoPauseStartTime, LocalTime autoPauseEndTime) {
    this.shopId = shopId;
    this.operationDay = operationDay;
    this.operationStartTime = operationStartTime;
    this.operationEndTime = operationEndTime;
    this.isClosedDay = isClosedDay;
    this.isUsedAutoPause = isUsedAutoPause;
    this.autoPauseStartTime = autoPauseStartTime;
    this.autoPauseEndTime = autoPauseEndTime;
    this.isPublished = true;
  }

  public void unPublish() {
    this.isPublished = false;
  }

}
