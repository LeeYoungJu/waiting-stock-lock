package co.wadcorp.waiting.data.domain.settings.remote;

import static co.wadcorp.libs.stream.StreamUtils.convertToMap;

import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 원격 매장 운영시간(RemoteOperationTimeSettingsEntity)에 대한 일급컬렉션 (매장 단위)
 */
public class ShopRemoteOperationTimeSettings {

  public static final ShopRemoteOperationTimeSettings EMPTY = new ShopRemoteOperationTimeSettings(
      List.of());

  private final Map<OperationDay, RemoteOperationTimeSettingsEntity> operationTimeSettingsMap;

  private ShopRemoteOperationTimeSettings(List<RemoteOperationTimeSettingsEntity> settingsList) {
    this.operationTimeSettingsMap = convertToMap(settingsList,
        RemoteOperationTimeSettingsEntity::getOperationDay);
  }

  public static ShopRemoteOperationTimeSettings of(RemoteOperationTimeSettingsEntity settings) {
    return of(List.of(settings));
  }

  public static ShopRemoteOperationTimeSettings of(
      List<RemoteOperationTimeSettingsEntity> settingsList) {
    ShopRemoteOperationTimeSettings shopSettings = new ShopRemoteOperationTimeSettings(List.of());
    settingsList.forEach(shopSettings::put);

    return shopSettings;
  }

  public ShopRemoteOperationTimeSettings put(RemoteOperationTimeSettingsEntity settings) {
    OperationDay operationDay = settings.getOperationDay();
    if (operationTimeSettingsMap.containsKey(operationDay)) {
      throw new IllegalArgumentException(
          String.format("요일 설정이 중복입니다. shopId=%s", settings.getShopId())
      );
    }

    this.operationTimeSettingsMap.put(operationDay, settings);
    return this;
  }

  public boolean notExistRemoteSettingsFor(LocalDate operationDate) {
    return isEmpty() || findSettingsBy(operationDate) == RemoteOperationTimeSettingsEntity.EMPTY;
  }

  public boolean isUsedAutoPause(LocalDate operationDate) {
    RemoteOperationTimeSettingsEntity remoteOperationTimeSettings = findSettingsBy(operationDate);
    return remoteOperationTimeSettings.isUsedAutoPause();
  }

  public boolean isNotUsedAutoPause(LocalDate operationDate) {
    return !isUsedAutoPause(operationDate);
  }

  public boolean isClosedDay(LocalDate operationDate) {
    RemoteOperationTimeSettingsEntity remoteOperationTimeSettings = findSettingsBy(operationDate);
    return remoteOperationTimeSettings.isClosedDay();
  }

  public LocalTime getOperationStartTime(LocalDate operationDate) {
    RemoteOperationTimeSettingsEntity remoteOperationTimeSettings = findSettingsBy(operationDate);
    return remoteOperationTimeSettings.getOperationStartTime();
  }

  public LocalTime getOperationEndTime(LocalDate operationDate) {
    RemoteOperationTimeSettingsEntity remoteOperationTimeSettings = findSettingsBy(operationDate);
    return remoteOperationTimeSettings.getOperationEndTime();
  }

  public LocalTime getAutoPauseStartTime(LocalDate operationDate) {
    RemoteOperationTimeSettingsEntity remoteOperationTimeSettings = findSettingsBy(operationDate);
    return remoteOperationTimeSettings.getAutoPauseStartTime();
  }

  public LocalTime getAutoPauseEndTime(LocalDate operationDate) {
    RemoteOperationTimeSettingsEntity remoteOperationTimeSettings = findSettingsBy(operationDate);
    return remoteOperationTimeSettings.getAutoPauseEndTime();
  }

  private RemoteOperationTimeSettingsEntity findSettingsBy(LocalDate operationDate) {
    OperationDay operationDay = OperationDay.findBy(operationDate);

    return operationTimeSettingsMap.getOrDefault(operationDay,
        RemoteOperationTimeSettingsEntity.EMPTY);
  }

  private boolean isEmpty() {
    return this == EMPTY;
  }

}
