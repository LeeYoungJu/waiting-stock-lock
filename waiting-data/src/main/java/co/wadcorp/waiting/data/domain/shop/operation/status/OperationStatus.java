package co.wadcorp.waiting.data.domain.shop.operation.status;

import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import co.wadcorp.waiting.data.query.waiting.dto.ShopOperationInfoDto;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 매장의 실제 Real-Time 운영 상태
 */
public enum OperationStatus {

  OPEN("영업 중"),
  BY_PASS("바로 입장"),
  PAUSE("일시 중지"),
  CLOSED("영업 종료");

  private final String value;

  OperationStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static OperationStatus find(ShopOperationInfoDto shopOperationInfoDto,
      ZonedDateTime nowDateTime) {
    return findOperationStatus(
        shopOperationInfoDto.getRegistrableStatus(),
        shopOperationInfoDto.getOperationStartDateTime(),
        shopOperationInfoDto.getOperationEndDateTime(),
        shopOperationInfoDto.getAutoPauseStartDateTime(),
        shopOperationInfoDto.getAutoPauseEndDateTime(),
        shopOperationInfoDto.getManualPauseStartDateTime(),
        shopOperationInfoDto.getManualPauseEndDateTime(),
        nowDateTime
    );
  }

  public static OperationStatus find(ShopOperationInfoEntity shopOperationInfoEntity,
      ZonedDateTime nowDateTime) {
    return findOperationStatus(
        shopOperationInfoEntity.getRegistrableStatus(),
        shopOperationInfoEntity.getOperationStartDateTime(),
        shopOperationInfoEntity.getOperationEndDateTime(),
        shopOperationInfoEntity.getAutoPauseStartDateTime(),
        shopOperationInfoEntity.getAutoPauseEndDateTime(),
        shopOperationInfoEntity.getManualPauseStartDateTime(),
        shopOperationInfoEntity.getManualPauseEndDateTime(),
        nowDateTime
    );
  }

  public static OperationStatus findWithRemoteTime(ShopOperationInfoEntity shopOperationInfoEntity,
      ZonedDateTime nowDateTime) {
    OperationStatus operationStatus = findOperationStatus(
        shopOperationInfoEntity.getRegistrableStatus(),
        shopOperationInfoEntity.getOperationStartDateTime(),
        shopOperationInfoEntity.getOperationEndDateTime(),
        shopOperationInfoEntity.getAutoPauseStartDateTime(),
        shopOperationInfoEntity.getAutoPauseEndDateTime(),
        shopOperationInfoEntity.getManualPauseStartDateTime(),
        shopOperationInfoEntity.getManualPauseEndDateTime(),
        nowDateTime
    );

    if (operationStatus == OperationStatus.PAUSE || operationStatus == OperationStatus.CLOSED) {
      return operationStatus;
    }

    ZonedDateTime remoteOperationStartDateTime = shopOperationInfoEntity.getRemoteOperationStartDateTime();
    ZonedDateTime remoteOperationEndDateTime = shopOperationInfoEntity.getRemoteOperationEndDateTime();
    if (nowDateTime.isBefore(remoteOperationStartDateTime)
        || nowDateTime.isAfter(remoteOperationEndDateTime)) {
      return CLOSED;
    }

    ZonedDateTime remoteAutoPauseStartDateTime = shopOperationInfoEntity.getRemoteAutoPauseStartDateTime();
    ZonedDateTime remoteAutoPauseEndDateTime = shopOperationInfoEntity.getRemoteAutoPauseEndDateTime();
    if (Objects.nonNull(remoteAutoPauseStartDateTime)
        && Objects.nonNull(remoteAutoPauseEndDateTime)
        && nowDateTime.isAfter(remoteAutoPauseStartDateTime)
        && nowDateTime.isBefore(remoteAutoPauseEndDateTime)) {
      return PAUSE;
    }

    return operationStatus;
  }

  private static OperationStatus findOperationStatus(RegistrableStatus registrableStatus,
      ZonedDateTime operationStartDateTime, ZonedDateTime operationEndDateTime,
      ZonedDateTime autoPauseStartDateTime, ZonedDateTime autoPauseEndDateTime,
      ZonedDateTime manualPauseStartDateTime, ZonedDateTime manualPauseEndDateTime,
      ZonedDateTime nowDateTime) {

    if (registrableStatus == RegistrableStatus.CLOSED) {
      return CLOSED;
    }

    if (registrableStatus == RegistrableStatus.BY_PASS) {
      return BY_PASS;
    }

    if (nowDateTime.isBefore(operationStartDateTime) || nowDateTime.isAfter(operationEndDateTime)) {
      return CLOSED;
    }

    if (Objects.nonNull(manualPauseStartDateTime)
        && Objects.nonNull(manualPauseEndDateTime)
        && nowDateTime.isAfter(manualPauseStartDateTime)
        && nowDateTime.isBefore(manualPauseEndDateTime)) {
      return PAUSE;
    }

    if (Objects.nonNull(manualPauseStartDateTime)
        && Objects.isNull(manualPauseEndDateTime)
        && nowDateTime.isAfter(manualPauseStartDateTime)) {
      return PAUSE;
    }

    if (Objects.nonNull(autoPauseStartDateTime)
        && Objects.nonNull(autoPauseEndDateTime)
        && nowDateTime.isAfter(autoPauseStartDateTime)
        && nowDateTime.isBefore(autoPauseEndDateTime)) {
      return PAUSE;
    }

    return OPEN;
  }
}
