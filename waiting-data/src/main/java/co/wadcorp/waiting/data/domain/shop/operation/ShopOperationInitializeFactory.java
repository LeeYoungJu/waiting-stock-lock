package co.wadcorp.waiting.data.domain.shop.operation;

import static co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity.DEFAULT_OPERATION_END_TIME;
import static co.wadcorp.waiting.shared.util.OperationDateTimeUtils.getCalculateOperationDateTime;

import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.AutoPauseSettings.PauseReason;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsData.OperationTimeForDay;
import co.wadcorp.waiting.data.domain.settings.OperationTimeSettingsEntity;
import co.wadcorp.waiting.data.domain.settings.remote.ShopRemoteOperationTimeSettings;
import co.wadcorp.waiting.data.domain.shop.operation.pause.AutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.pause.RemoteAutoPauseInfo;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity.ShopOperationInfoEntityBuilder;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopOperationInitializeFactory {

  public static ShopOperationInfoEntity initialize(
      OperationTimeSettingsEntity operationTimeSettings, LocalDate operationDate) {
    return initialize(operationTimeSettings, ShopRemoteOperationTimeSettings.EMPTY, operationDate);
  }

  public static ShopOperationInfoEntity initialize(
      OperationTimeSettingsEntity operationTimeSettings,
      ShopRemoteOperationTimeSettings remoteOperationTimeSettings, LocalDate operationDate) {
    ShopOperationInfoEntityBuilder operationInfoEntityBuilder = ShopOperationInfoEntity.builder();

    operationInfoEntityBuilder
        .shopId(operationTimeSettings.getShopId())
        .operationDate(operationDate);

    setByRegistrableStatus(operationTimeSettings, operationDate, operationInfoEntityBuilder);

    setAutoPauseSettings(operationTimeSettings, operationDate, operationInfoEntityBuilder);
    setRemoteAutoPauseSettings(remoteOperationTimeSettings, operationDate,
        operationInfoEntityBuilder);

    setOperationDateTime(operationTimeSettings, operationDate, operationInfoEntityBuilder);
    setRemoteOperationDateTime(remoteOperationTimeSettings, operationDate,
        operationInfoEntityBuilder);

    return operationInfoEntityBuilder.build();
  }

  private static void setByRegistrableStatus(OperationTimeSettingsEntity operationTimeSettings,
      LocalDate operationDate, ShopOperationInfoEntityBuilder operationInfoEntityBuilder) {
    RegistrableStatus status = operationTimeSettings.findRegistrableStatus(operationDate);
    if (status == RegistrableStatus.CLOSED) {
      operationInfoEntityBuilder.closedReason(ClosedReason.CLOSED_DAY);
    }

    operationInfoEntityBuilder.registrableStatus(status);
  }

  private static void setAutoPauseSettings(OperationTimeSettingsEntity operationTimeSettings,
      LocalDate operationDate, ShopOperationInfoEntityBuilder operationInfoEntityBuilder) {
    if (operationTimeSettings.notUsedAutoPause()) {
      return;
    }

    PauseReason defaultPauseReason = operationTimeSettings.getDefaultPauseReason();

    operationInfoEntityBuilder
        .autoPauseInfo(AutoPauseInfo.builder()
            .autoPauseStartDateTime(getCalculateOperationDateTime(
                operationDate, operationTimeSettings.getAutoPauseStartTime()
            ))
            .autoPauseEndDateTime(getCalculateOperationDateTime(
                operationDate, operationTimeSettings.getAutoPauseEndTime()
            ))
            .autoPauseReasonId(defaultPauseReason.getId())
            .autoPauseReason(defaultPauseReason.getReason())
            .build()
        );
  }

  private static void setRemoteAutoPauseSettings(
      ShopRemoteOperationTimeSettings remoteOperationTimeSettings, LocalDate operationDate,
      ShopOperationInfoEntityBuilder operationInfoEntityBuilder) {
    if (remoteOperationTimeSettings.notExistRemoteSettingsFor(operationDate)) {
      return;
    }
    if (remoteOperationTimeSettings.isNotUsedAutoPause(operationDate)) {
      return;
    }

    operationInfoEntityBuilder
        .remoteAutoPauseInfo(RemoteAutoPauseInfo.builder()
            .remoteAutoPauseStartDateTime(getCalculateOperationDateTime(
                operationDate, remoteOperationTimeSettings.getAutoPauseStartTime(operationDate)
            ))
            .remoteAutoPauseEndDateTime(getCalculateOperationDateTime(
                operationDate, remoteOperationTimeSettings.getAutoPauseEndTime(operationDate)
            ))
            .build()
        );
  }

  private static void setOperationDateTime(OperationTimeSettingsEntity operationTimeSettings,
      LocalDate operationDate, ShopOperationInfoEntityBuilder operationInfoEntityBuilder) {
    OperationTimeForDay operationTimeForDay = operationTimeSettings.findOperationTimeForDay(
        OperationDay.findBy(operationDate)
    );

    operationInfoEntityBuilder
        .operationStartDateTime(getCalculateOperationDateTime(
            operationDate, operationTimeForDay.getOperationStartTime()
        ))
        .operationEndDateTime(getCalculateOperationDateTime(
            operationDate, operationTimeForDay.getOperationEndTime()
        ));
  }

  private static void setRemoteOperationDateTime(
      ShopRemoteOperationTimeSettings remoteOperationTimeSettings,
      LocalDate operationDate, ShopOperationInfoEntityBuilder operationInfoEntityBuilder) {
    if (remoteOperationTimeSettings.notExistRemoteSettingsFor(operationDate)) {
      return;
    }

    // 원격 휴무일인 경우 시작/종료 시간을 임의의 같은 시간으로 세팅한다. = 운영이 불가하다는 의미
    // (아무 시간이나 상관 없지만 혹시 몰라 공식 운영종료 시간을 사용)
    if (remoteOperationTimeSettings.isClosedDay(operationDate)) {
      ZonedDateTime operationDateTime = getCalculateOperationDateTime(operationDate,
          DEFAULT_OPERATION_END_TIME);

      operationInfoEntityBuilder
          .remoteOperationStartDateTime(operationDateTime)
          .remoteOperationEndDateTime(operationDateTime);
      return;
    }

    operationInfoEntityBuilder
        .remoteOperationStartDateTime(getCalculateOperationDateTime(
            operationDate, remoteOperationTimeSettings.getOperationStartTime(operationDate)
        ))
        .remoteOperationEndDateTime(getCalculateOperationDateTime(
            operationDate, remoteOperationTimeSettings.getOperationEndTime(operationDate)
        ));
  }

}
