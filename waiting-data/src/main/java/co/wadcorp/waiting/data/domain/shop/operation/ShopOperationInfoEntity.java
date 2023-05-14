package co.wadcorp.waiting.data.domain.shop.operation;

import static co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity.DEFAULT_OPERATION_END_TIME;
import static co.wadcorp.waiting.shared.util.OperationDateTimeUtils.getCalculateOperationDateTime;

import co.wadcorp.waiting.data.domain.settings.OperationTimeForDaysChangeChecker;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDay;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.ShopRemoteOperationTimeSettings;
import co.wadcorp.waiting.data.domain.shop.operation.pause.AutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.pause.ManualPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.pause.RemoteAutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.enums.OperationDay;
import co.wadcorp.waiting.data.support.BaseEntity;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cw_shop_operation_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopOperationInfoEntity extends BaseEntity {

  public static final ShopOperationInfoEntity EMPTY_OPERATION_INFO = new ShopOperationInfoEntity();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long seq;

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

  @Builder
  private ShopOperationInfoEntity(String shopId, LocalDate operationDate,
      RegistrableStatus registrableStatus, ZonedDateTime operationStartDateTime,
      ZonedDateTime operationEndDateTime, ZonedDateTime remoteOperationStartDateTime,
      ZonedDateTime remoteOperationEndDateTime, ManualPauseInfo manualPauseInfo,
      AutoPauseInfo autoPauseInfo, RemoteAutoPauseInfo remoteAutoPauseInfo,
      ClosedReason closedReason) {
    this.shopId = shopId;
    this.operationDate = operationDate;
    this.registrableStatus = registrableStatus;
    this.operationStartDateTime = operationStartDateTime;
    this.operationEndDateTime = operationEndDateTime;
    this.remoteOperationStartDateTime = remoteOperationStartDateTime != null
        ? remoteOperationStartDateTime
        : operationStartDateTime;
    this.remoteOperationEndDateTime = remoteOperationEndDateTime != null
        ? remoteOperationEndDateTime
        : operationEndDateTime;
    this.manualPauseInfo = manualPauseInfo;
    this.autoPauseInfo = autoPauseInfo;
    updateRemoteAutoPause(remoteAutoPauseInfo, autoPauseInfo);
    this.closedReason = closedReason;
  }

  public void open() {
    this.registrableStatus = RegistrableStatus.OPEN;
    this.closedReason = null;
    this.manualPauseInfo = null;
  }

  public void close() {
    this.registrableStatus = RegistrableStatus.CLOSED;
    this.closedReason = ClosedReason.MANUAL;
    this.manualPauseInfo = null;
  }

  public void byPass() {
    this.registrableStatus = RegistrableStatus.BY_PASS;
    this.closedReason = null;
    this.manualPauseInfo = null;
  }

  public void closeByClosedDay() {
    this.registrableStatus = RegistrableStatus.CLOSED;
    this.closedReason = ClosedReason.CLOSED_DAY;
    this.manualPauseInfo = null;
  }

  public void pause(String pauseReasonId, String pauseReason, Integer pausePeriod) {
    ZonedDateTime pauseStart = ZonedDateTimeUtils.nowOfSeoul();
    ZonedDateTime pauseEnd = pausePeriod == -1 ? null : pauseStart.plusMinutes(pausePeriod);

    this.registrableStatus = RegistrableStatus.OPEN;
    this.closedReason = null;

    this.manualPauseInfo = ManualPauseInfo.builder()
        .manualPauseStartDateTime(pauseStart)
        .manualPauseEndDateTime(pauseEnd)
        .manualPauseReasonId(pauseReasonId)
        .manualPauseReason(pauseReason)
        .build();
  }

  public void updateOperationStartDateTime(ZonedDateTime operationStartDateTime) {
    this.operationStartDateTime = operationStartDateTime;
  }

  public void updateOperationEndDateTime(ZonedDateTime operationEndDateTime) {
    this.operationEndDateTime = operationEndDateTime;
  }

  public boolean isBeforeOperationDateTime(ZonedDateTime nowLocalDateTime) {
    return nowLocalDateTime.isBefore(this.operationStartDateTime);
  }

  public boolean isAfterOperationEndDateTime(ZonedDateTime nowLocalDateTime) {
    return nowLocalDateTime.isAfter(this.operationEndDateTime);
  }

  public boolean isBetweenAutoPauseRange(ZonedDateTime nowLocalDateTime) {
    if (autoPauseInfo == null) {
      return false;
    }
    return autoPauseInfo.isBetweenAutoPauseRange(nowLocalDateTime);
  }

  public void clearAutoPauseInfo() {
    autoPauseInfo = null;
  }

  public void updateOperationTimeSettings(OperationTimeSettingsEntity operationTimeSettings,
      OperationTimeForDaysChangeChecker changeChecker) {
    setAutoPauseSettings(operationTimeSettings);
    setOperationDateTimes(operationTimeSettings, changeChecker);
  }

  public void updateRemoteOperationTimeSettings(
      ShopRemoteOperationTimeSettings shopRemoteOperationTimeSettings) {
    setRemoteAutoPauseSettings(shopRemoteOperationTimeSettings);
    setRemoteOperationDateTimes(shopRemoteOperationTimeSettings);
  }

  public ClosedReason findClosedReason(ZonedDateTime nowDateTime) {
    if (Objects.isNull(this.closedReason)
        && !ZonedDateTimeUtils.isBetween(nowDateTime, operationStartDateTime, operationEndDateTime)
    ) {
      return ClosedReason.OPERATION_HOUR_CLOSED;
    }
    return this.closedReason;
  }

  public ZonedDateTime getRemoteOperationStartDateTime() {
    if (this.remoteOperationStartDateTime == null) {
      return operationStartDateTime;
    }
    return remoteOperationStartDateTime;
  }

  public ZonedDateTime getRemoteOperationEndDateTime() {
    if (this.remoteOperationEndDateTime == null) {
      return operationEndDateTime;
    }
    return remoteOperationEndDateTime;
  }

  public ZonedDateTime getManualPauseStartDateTime() {
    if (this.manualPauseInfo == null) {
      return null;
    }
    return this.manualPauseInfo.getManualPauseStartDateTime();
  }

  public ZonedDateTime getManualPauseEndDateTime() {
    if (this.manualPauseInfo == null) {
      return null;
    }
    return this.manualPauseInfo.getManualPauseEndDateTime();
  }

  public String getManualPauseReasonId() {
    if (this.manualPauseInfo == null) {
      return null;
    }
    return this.manualPauseInfo.getManualPauseReasonId();
  }

  public String getManualPauseReason() {
    if (this.manualPauseInfo == null) {
      return null;
    }
    return this.manualPauseInfo.getManualPauseReason();
  }

  public ZonedDateTime getAutoPauseStartDateTime() {
    if (this.autoPauseInfo == null) {
      return null;
    }
    return this.autoPauseInfo.getAutoPauseStartDateTime();
  }

  public ZonedDateTime getAutoPauseEndDateTime() {
    if (this.autoPauseInfo == null) {
      return null;
    }
    return this.autoPauseInfo.getAutoPauseEndDateTime();
  }

  public String getAutoPauseReasonId() {
    if (this.autoPauseInfo == null) {
      return null;
    }
    return this.autoPauseInfo.getAutoPauseReasonId();
  }

  public String getAutoPauseReason() {
    if (this.autoPauseInfo == null) {
      return null;
    }
    return this.autoPauseInfo.getAutoPauseReason();
  }

  public ZonedDateTime getRemoteAutoPauseStartDateTime() {
    if (this.remoteAutoPauseInfo == null) {
      return null;
    }
    return this.remoteAutoPauseInfo.getRemoteAutoPauseStartDateTime();
  }

  public ZonedDateTime getRemoteAutoPauseEndDateTime() {
    if (this.remoteAutoPauseInfo == null) {
      return null;
    }
    return this.remoteAutoPauseInfo.getRemoteAutoPauseEndDateTime();
  }

  private void setAutoPauseSettings(OperationTimeSettingsEntity operationTimeSettings) {
    if (operationTimeSettings.notUsedAutoPause()) {
      clearAutoPauseInfo();
      return;
    }

    ZonedDateTime autoPauseStartDateTime = getCalculateOperationDateTime(operationDate,
        operationTimeSettings.getAutoPauseStartTime());
    ZonedDateTime autoPauseEndDateTime = getCalculateOperationDateTime(operationDate,
        operationTimeSettings.getAutoPauseEndTime());
    PauseReason defaultPauseReason = operationTimeSettings.getDefaultPauseReason();

    pauseAuto(autoPauseStartDateTime, autoPauseEndDateTime, defaultPauseReason.getId(),
        defaultPauseReason.getReason());
  }

  private void pauseAuto(ZonedDateTime pauseStart, ZonedDateTime pauseEnd, String pauseReasonId,
      String pauseReason) {

    this.closedReason = null;

    this.autoPauseInfo = AutoPauseInfo.builder()
        .autoPauseStartDateTime(pauseStart)
        .autoPauseEndDateTime(pauseEnd)
        .autoPauseReasonId(pauseReasonId)
        .autoPauseReason(pauseReason)
        .build();
  }

  private void setOperationDateTimes(OperationTimeSettingsEntity operationTimeSettings,
      OperationTimeForDaysChangeChecker changeChecker) {
    OperationTimeForDay operationTimeForDay = operationTimeSettings.findOperationTimeForDay(
        OperationDay.findBy(operationDate));

    settingOperationStatus(operationTimeForDay);

    if (changeChecker.isThereChangeInDay(operationDate.getDayOfWeek().name())) {
      updateOperationDateTime(
          getCalculateOperationDateTime(operationDate, operationTimeForDay.getOperationStartTime()),
          getCalculateOperationDateTime(operationDate, operationTimeForDay.getOperationEndTime())
      );
    }
  }

  private void settingOperationStatus(OperationTimeForDay operationTimeForDay) {
    if (operationTimeForDay.getIsClosedDay()) {
      this.closeByClosedDay();
    }
  }

  private void updateOperationDateTime(ZonedDateTime operationStartDateTime,
      ZonedDateTime operationEndDateTime) {

    updateOperationStartDateTime(operationStartDateTime);
    updateOperationEndDateTime(operationEndDateTime);
  }

  private void setRemoteAutoPauseSettings(
      ShopRemoteOperationTimeSettings shopRemoteOperationTimeSettings) {
    if (shopRemoteOperationTimeSettings.notExistRemoteSettingsFor(operationDate)) {
      clearRemoteAutoPauseInfo();
      return;
    }
    if (shopRemoteOperationTimeSettings.isNotUsedAutoPause(operationDate)) {
      clearRemoteAutoPauseInfo();
      return;
    }

    updateRemoteAutoPauseDateTimes(
        getCalculateOperationDateTime(
            operationDate, shopRemoteOperationTimeSettings.getAutoPauseStartTime(operationDate)
        ),
        getCalculateOperationDateTime(
            operationDate, shopRemoteOperationTimeSettings.getAutoPauseEndTime(operationDate)
        )
    );
  }

  private void setRemoteOperationDateTimes(
      ShopRemoteOperationTimeSettings shopRemoteOperationTimeSettings) {
    if (shopRemoteOperationTimeSettings.notExistRemoteSettingsFor(operationDate)) {
      clearRemoteOperationDateTime();
      return;
    }

    // 원격 휴무일인 경우 시작/종료 시간을 임의의 같은 시간으로 세팅한다. = 운영이 불가하다는 의미
    // (아무 시간이나 상관 없지만 혹시 몰라 공식 운영종료 시간을 사용)
    if (shopRemoteOperationTimeSettings.isClosedDay(operationDate)) {
      ZonedDateTime operationDateTime = getCalculateOperationDateTime(operationDate,
          DEFAULT_OPERATION_END_TIME);

      updateRemoteOperationDateTimes(operationDateTime, operationDateTime);
      return;
    }

    updateRemoteOperationDateTimes(
        getCalculateOperationDateTime(
            operationDate, shopRemoteOperationTimeSettings.getOperationStartTime(operationDate)
        ),
        getCalculateOperationDateTime(
            operationDate, shopRemoteOperationTimeSettings.getOperationEndTime(operationDate)
        )
    );
  }

  private void clearRemoteAutoPauseInfo() {
    updateRemoteAutoPause(null, this.autoPauseInfo);
  }

  private void updateRemoteAutoPauseDateTimes(ZonedDateTime remoteAutoPauseStartDateTime,
      ZonedDateTime remoteAutoPauseEndDateTime) {
    this.remoteAutoPauseInfo = RemoteAutoPauseInfo.builder()
        .remoteAutoPauseStartDateTime(remoteAutoPauseStartDateTime)
        .remoteAutoPauseEndDateTime(remoteAutoPauseEndDateTime)
        .build();
  }

  private void updateRemoteAutoPause(RemoteAutoPauseInfo remoteAutoPauseInfo,
      AutoPauseInfo autoPauseInfo) {
    this.remoteAutoPauseInfo = remoteAutoPauseInfo;

    if (remoteAutoPauseInfo == null && autoPauseInfo != null) {
      this.remoteAutoPauseInfo = RemoteAutoPauseInfo.builder()
          .remoteAutoPauseStartDateTime(autoPauseInfo.getAutoPauseStartDateTime())
          .remoteAutoPauseEndDateTime(autoPauseInfo.getAutoPauseEndDateTime())
          .build();
    }
  }

  private void clearRemoteOperationDateTime() {
    this.remoteOperationStartDateTime = this.operationStartDateTime;
    this.remoteOperationEndDateTime = this.operationEndDateTime;
  }

  private void updateRemoteOperationDateTimes(ZonedDateTime remoteOperationStartDateTime,
      ZonedDateTime remoteOperationEndDateTime) {
    this.remoteOperationStartDateTime = remoteOperationStartDateTime;
    this.remoteOperationEndDateTime = remoteOperationEndDateTime;
  }

}
