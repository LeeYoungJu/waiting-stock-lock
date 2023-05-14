package co.wadcorp.waiting.data.query.waiting.dto;

import co.wadcorp.waiting.data.domain.shop.operation.ClosedReason;
import co.wadcorp.waiting.data.domain.shop.operation.status.RegistrableStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ShopOperationInfoDto {

  private final LocalDate operationDate;
  private final RegistrableStatus registrableStatus;
  private final ZonedDateTime operationStartDateTime;
  private final ZonedDateTime operationEndDateTime;
  private final ZonedDateTime manualPauseStartDateTime;
  private final ZonedDateTime manualPauseEndDateTime;
  private final String manualPauseReasonId;
  private final String manualPauseReason;
  private final ZonedDateTime autoPauseStartDateTime;
  private final ZonedDateTime autoPauseEndDateTime;
  private final String autoPauseReasonId;
  private final String autoPauseReason;
  private final ClosedReason closedReason;

  @Builder
  @QueryProjection
  public ShopOperationInfoDto(LocalDate operationDate,
      RegistrableStatus registrableStatus, ZonedDateTime operationStartDateTime,
      ZonedDateTime operationEndDateTime, ZonedDateTime manualPauseStartDateTime,
      ZonedDateTime manualPauseEndDateTime, String manualPauseReasonId, String manualPauseReason,
      ZonedDateTime autoPauseStartDateTime, ZonedDateTime autoPauseEndDateTime,
      String autoPauseReasonId, String autoPauseReason, ClosedReason closedReason) {
    this.operationDate = operationDate;
    this.registrableStatus = registrableStatus;
    this.operationStartDateTime = operationStartDateTime;
    this.operationEndDateTime = operationEndDateTime;
    this.manualPauseStartDateTime = manualPauseStartDateTime;
    this.manualPauseEndDateTime = manualPauseEndDateTime;
    this.manualPauseReasonId = manualPauseReasonId;
    this.manualPauseReason = manualPauseReason;
    this.autoPauseStartDateTime = autoPauseStartDateTime;
    this.autoPauseEndDateTime = autoPauseEndDateTime;
    this.autoPauseReasonId = autoPauseReasonId;
    this.autoPauseReason = autoPauseReason;
    this.closedReason = closedReason;
  }
}