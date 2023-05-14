package co.wadcorp.waiting.api.internal.controller.shop.dto.request;

import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalRemoteShopOperationTimeSettingsServiceRequest;
import co.wadcorp.waiting.api.internal.service.shop.dto.request.InternalRemoteShopOperationTimeSettingsServiceRequest.RemoteOperationTimeSettingsServiceDto;
import co.wadcorp.waiting.data.enums.OperationDay;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InternalRemoteShopOperationTimeSettingsRequest {

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate settingStartDate;

  @Valid
  @NotEmpty(message = "원격 운영시간 세팅은 1건 이상 등록해야 합니다.")
  private List<RemoteOperationTimeSettingsDto> settings;

  @Builder
  private InternalRemoteShopOperationTimeSettingsRequest(LocalDate settingStartDate,
      List<RemoteOperationTimeSettingsDto> settings) {
    this.settingStartDate = settingStartDate;
    this.settings = settings;
  }

  public InternalRemoteShopOperationTimeSettingsServiceRequest toServiceRequest() {
    return InternalRemoteShopOperationTimeSettingsServiceRequest.builder()
        .settingStartDate(settingStartDate != null
            ? settingStartDate
            : OperationDateUtils.getOperationDateFromNow()
        )
        .settings(settings.stream()
            .map(RemoteOperationTimeSettingsDto::toServiceDto)
            .toList()
        )
        .build();
  }

  @Getter
  @NoArgsConstructor
  public static class RemoteOperationTimeSettingsDto {

    @NotNull(message = "요일은 필수입니다.")
    private OperationDay operationDay;

    @NotNull(message = "운영 시작 시각은 필수입니다.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime operationStartTime;

    @NotNull(message = "운영 종료 시각은 필수입니다.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime operationEndTime;

    @NotNull(message = "휴무일 여부는 필수입니다.")
    private Boolean isClosedDay;

    @NotNull(message = "자동 일시정지 사용 여부는 필수입니다.")
    private Boolean isUsedAutoPause;

    @NotNull(message = "자동 일시정지 시작 시각은 필수입니다.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime autoPauseStartTime;

    @NotNull(message = "자동 일시정지 종료 시각은 필수입니다.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime autoPauseEndTime;

    @Builder
    private RemoteOperationTimeSettingsDto(OperationDay operationDay, LocalTime operationStartTime,
        LocalTime operationEndTime, Boolean isClosedDay, Boolean isUsedAutoPause,
        LocalTime autoPauseStartTime, LocalTime autoPauseEndTime) {
      this.operationDay = operationDay;
      this.operationStartTime = operationStartTime;
      this.operationEndTime = operationEndTime;
      this.isClosedDay = isClosedDay;
      this.isUsedAutoPause = isUsedAutoPause;
      this.autoPauseStartTime = autoPauseStartTime;
      this.autoPauseEndTime = autoPauseEndTime;
    }

    public RemoteOperationTimeSettingsServiceDto toServiceDto() {
      return RemoteOperationTimeSettingsServiceDto.builder()
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
