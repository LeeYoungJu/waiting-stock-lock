package co.wadcorp.waiting.api.internal.service.shop.dto.request;

import co.wadcorp.waiting.data.domain.settings.remote.RemoteOperationTimeSettingsEntity;
import co.wadcorp.waiting.data.enums.OperationDay;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InternalRemoteShopOperationTimeSettingsServiceRequest {

  private LocalDate settingStartDate;
  private List<RemoteOperationTimeSettingsServiceDto> settings;

  @Builder
  private InternalRemoteShopOperationTimeSettingsServiceRequest(LocalDate settingStartDate,
      List<RemoteOperationTimeSettingsServiceDto> settings) {
    this.settingStartDate = settingStartDate;
    this.settings = settings;
  }

  public List<RemoteOperationTimeSettingsEntity> toEntities(String shopId) {
    if (haveDuplicateOperationDaySettings()) {
      throw new IllegalArgumentException("요일 설정이 중복입니다.");
    }

    return settings.stream()
        .map(setting -> setting.toEntity(shopId))
        .toList();
  }

  private boolean haveDuplicateOperationDaySettings() {
    long distinctCount = settings.stream()
        .map(RemoteOperationTimeSettingsServiceDto::getOperationDay)
        .distinct()
        .count();
    return distinctCount != settings.size();
  }

  @Getter
  public static class RemoteOperationTimeSettingsServiceDto {

    private final OperationDay operationDay;
    private final LocalTime operationStartTime;
    private final LocalTime operationEndTime;
    private final boolean isClosedDay;
    private final boolean isUsedAutoPause;
    private final LocalTime autoPauseStartTime;
    private final LocalTime autoPauseEndTime;

    @Builder
    private RemoteOperationTimeSettingsServiceDto(OperationDay operationDay,
        LocalTime operationStartTime, LocalTime operationEndTime, boolean isClosedDay,
        boolean isUsedAutoPause, LocalTime autoPauseStartTime, LocalTime autoPauseEndTime) {
      this.operationDay = operationDay;
      this.operationStartTime = operationStartTime;
      this.operationEndTime = operationEndTime;
      this.isClosedDay = isClosedDay;
      this.isUsedAutoPause = isUsedAutoPause;
      this.autoPauseStartTime = autoPauseStartTime;
      this.autoPauseEndTime = autoPauseEndTime;
    }

    public RemoteOperationTimeSettingsEntity toEntity(String shopId) {
      return RemoteOperationTimeSettingsEntity.builder()
          .shopId(shopId)
          .operationDay(operationDay)
          .operationStartTime(operationStartTime)
          .operationEndTime(operationEndTime)
          .isClosedDay(isClosedDay)
          .isUsedAutoPause(isUsedAutoPause)
          .autoPauseStartTime(autoPauseStartTime)
          .autoPauseEndTime(autoPauseEndTime)
          .build();
    }

  }

}
