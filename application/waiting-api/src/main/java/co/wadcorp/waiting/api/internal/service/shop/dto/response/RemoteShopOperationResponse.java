package co.wadcorp.waiting.api.internal.service.shop.dto.response;

import static co.wadcorp.libs.stream.StreamUtils.convert;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.settings.Precaution;
import co.wadcorp.waiting.data.domain.shop.operation.ClosedReason;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteShopOperationResponse {

  private final Long shopId;
  private final String operationDate;

  @Getter(value = AccessLevel.PRIVATE)
  @JsonProperty("isUsedWaiting")
  private final boolean isUsedWaiting; // 현장 웨이팅 사용 여부 (가맹 여부)

  @Getter(value = AccessLevel.PRIVATE)
  @JsonProperty("isUsedRemoteWaiting")
  private final boolean isUsedRemoteWaiting; // 원격 웨이팅 사용 여부

  private final OperationStatus operationStatus; // 영업 상태
  private final String operationStartDateTime;
  private final String operationEndDateTime;
  private final String pauseStartDateTime;
  private final String pauseEndDateTime;
  private final String pauseReasonId;
  private final String pauseReason;


  private final String autoPauseStartDateTime;
  private final String autoPauseEndDateTime;
  private final String autoPauseReasonId;
  private final String autoPauseReason;
  private final String manualPauseStartDateTime;
  private final String manualPauseEndDateTime;
  private final String manualPauseReasonId;
  private final String manualPauseReason;

  private final String remoteOperationStartDateTime;
  private final String remoteOperationEndDateTime;
  private final String remoteAutoPauseStartDateTime;
  private final String remoteAutoPauseEndDateTime;

  private final ClosedReason closedReason;
  private final int autoAlarmOrdering;

  @Getter(value = AccessLevel.PRIVATE)
  @JsonProperty("isUsedPrecautions")
  private boolean isUsedPrecautions;

  private final List<AppPrecautionVO> precautions = new ArrayList<>();
  private final String messagePrecaution;

  private final boolean disablePutOff;

  @Getter(value = AccessLevel.PRIVATE)
  @JsonProperty("isPossibleOrder")
  private final boolean isPossibleOrder;

  @Builder
  private RemoteShopOperationResponse(Long shopId, LocalDate operationDate, boolean isUsedWaiting,
      boolean isUsedRemoteWaiting, OperationStatus operationStatus,
      ZonedDateTime operationStartDateTime, ZonedDateTime operationEndDateTime,
      ZonedDateTime pauseStartDateTime, ZonedDateTime pauseEndDateTime,
      String pauseReasonId, String pauseReason, ZonedDateTime autoPauseStartDateTime,
      ZonedDateTime autoPauseEndDateTime, String autoPauseReasonId, String autoPauseReason,
      ZonedDateTime manualPauseStartDateTime, ZonedDateTime manualPauseEndDateTime,
      String manualPauseReasonId,
      String manualPauseReason, ClosedReason closedReason, int autoAlarmOrdering,
      boolean isUsedPrecautions, List<Precaution> precautions, String messagePrecaution,
      ZonedDateTime remoteOperationStartDateTime, ZonedDateTime remoteOperationEndDateTime,
      ZonedDateTime remoteAutoPauseStartDateTime, ZonedDateTime remoteAutoPauseEndDateTime,
      boolean disablePutOff, boolean isPossibleOrder
  ) {
    this.shopId = shopId;
    this.operationDate = ISO8601.formatAsDate(operationDate);
    this.isUsedWaiting = isUsedWaiting;
    this.isUsedRemoteWaiting = isUsedRemoteWaiting;
    this.operationStatus = operationStatus;
    this.operationStartDateTime = ISO8601.format(operationStartDateTime);
    this.operationEndDateTime = ISO8601.format(operationEndDateTime);
    this.pauseStartDateTime = ISO8601.format(pauseStartDateTime);
    this.pauseEndDateTime = ISO8601.format(pauseEndDateTime);
    this.pauseReasonId = pauseReasonId;
    this.pauseReason = pauseReason;
    this.autoPauseStartDateTime = ISO8601.format(autoPauseStartDateTime);
    this.autoPauseEndDateTime = ISO8601.format(autoPauseEndDateTime);
    this.autoPauseReasonId = autoPauseReasonId;
    this.autoPauseReason = autoPauseReason;
    this.manualPauseStartDateTime = ISO8601.format(manualPauseStartDateTime);
    this.manualPauseEndDateTime = ISO8601.format(manualPauseEndDateTime);
    this.manualPauseReasonId = manualPauseReasonId;
    this.manualPauseReason = manualPauseReason;
    this.closedReason = closedReason;
    this.autoAlarmOrdering = autoAlarmOrdering;
    this.isUsedPrecautions = isUsedPrecautions;
    if (precautions != null) {
      this.precautions.addAll(convert(precautions, AppPrecautionVO::convert));
    }
    this.messagePrecaution = messagePrecaution;
    this.remoteOperationStartDateTime = ISO8601.format(remoteOperationStartDateTime);
    this.remoteOperationEndDateTime = ISO8601.format(remoteOperationEndDateTime);
    this.remoteAutoPauseStartDateTime = ISO8601.format(remoteAutoPauseStartDateTime);
    this.remoteAutoPauseEndDateTime = ISO8601.format(remoteAutoPauseEndDateTime);
    this.disablePutOff = disablePutOff;
    this.isPossibleOrder = isPossibleOrder;
  }

  @Getter
  public static class AppPrecautionVO {

    private final String id;
    private final String content;

    @Builder
    private AppPrecautionVO(String id, String content) {
      this.id = id;
      this.content = content;
    }

    public static AppPrecautionVO convert(Precaution precautionVO) {
      return AppPrecautionVO.builder()
          .id(precautionVO.getId())
          .content(precautionVO.getContent())
          .build();
    }

  }

}
