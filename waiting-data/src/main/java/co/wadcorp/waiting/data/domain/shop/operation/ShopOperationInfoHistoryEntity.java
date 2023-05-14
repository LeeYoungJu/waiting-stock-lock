package co.wadcorp.waiting.data.domain.shop.operation;

import co.wadcorp.waiting.data.domain.shop.operation.pause.AutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.pause.ManualPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.pause.RemoteAutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.support.BaseHistoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cw_shop_operation_info_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopOperationInfoHistoryEntity extends BaseHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long seq;

  @Column(name = "shop_operation_info_seq")
  private Long shopOperationInfoSeq;

  @Column(name = "shop_id")
  private String shopId;

  @Column(name = "operation_date")
  private LocalDate operationDate;

  @Column(name = "registrable_status")
  @Enumerated(EnumType.STRING)
  private RegistrableStatus registrableStatus;

  @Column(name = "operation_start_date_time")
  private ZonedDateTime operationStartDateTime;

  @Column(name = "operation_end_date_time")
  private ZonedDateTime operationEndDateTime;

  @Column(name = "remote_operation_start_date_time")
  private ZonedDateTime remoteOperationStartDateTime;

  @Column(name = "remote_operation_end_date_time")
  private ZonedDateTime remoteOperationEndDateTime;

  @Embedded
  private ManualPauseInfo manualPauseInfo;

  @Embedded
  private AutoPauseInfo autoPauseInfo;

  @Embedded
  private RemoteAutoPauseInfo remoteAutoPauseInfo;

  @Column(name = "closed_reason")
  @Enumerated(EnumType.STRING)
  private ClosedReason closedReason;

  public static ShopOperationInfoHistoryEntity of(ShopOperationInfoEntity entity) {
    ShopOperationInfoHistoryEntity history = new ShopOperationInfoHistoryEntity();

    history.shopOperationInfoSeq = entity.getSeq();
    history.shopId = entity.getShopId();
    history.operationDate = entity.getOperationDate();
    history.registrableStatus = entity.getRegistrableStatus();
    history.operationStartDateTime = entity.getOperationStartDateTime();
    history.operationEndDateTime = entity.getOperationEndDateTime();
    history.remoteOperationStartDateTime = entity.getRemoteOperationStartDateTime();
    history.remoteOperationEndDateTime = entity.getRemoteOperationEndDateTime();
    history.manualPauseInfo = entity.getManualPauseInfo();
    history.autoPauseInfo = entity.getAutoPauseInfo();
    history.remoteAutoPauseInfo = entity.getRemoteAutoPauseInfo();
    history.closedReason = entity.getClosedReason();

    return history;
  }

}
