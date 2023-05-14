package co.wadcorp.waiting.api.model.waiting.vo;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.query.waiting.dto.ShopOperationInfoDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class ShopOperationInfoVO {

  private final String operationDate;
  private final OperationStatus operationStatus;
  private final String operationStartDateTime;
  private final String operationEndDateTime;
  private final String pauseStartDateTime;
  private final String pauseEndDateTime;
  private final String pauseReasonId;
  private final String pauseReason;

  @Builder
  public ShopOperationInfoVO(LocalDate operationDate,
      OperationStatus operationStatus,
      ZonedDateTime operationStartDateTime, ZonedDateTime operationEndDateTime,
      ZonedDateTime pauseStartDateTime, ZonedDateTime pauseEndDateTime, String pauseReasonId, String pauseReason) {
    this.operationDate = operationDate.toString();
    this.operationStatus = operationStatus;
    this.operationStartDateTime = ISO8601.format(operationStartDateTime);
    this.operationEndDateTime = ISO8601.format(operationEndDateTime);
    this.pauseStartDateTime = ISO8601.format(pauseStartDateTime);
    this.pauseEndDateTime = ISO8601.format(pauseEndDateTime);
    this.pauseReasonId = pauseReasonId;
    this.pauseReason = pauseReason;
  }

  public static ShopOperationInfoVO toDto(ShopOperationInfoDto shopOperationInfoDto, ZonedDateTime nowDateTime) {
    String manualPauseReason = shopOperationInfoDto.getManualPauseReason();
    String autoPauseReason = shopOperationInfoDto.getAutoPauseReason();

    ShopOperationInfoVOBuilder shopOperationInfoVOBuilder = ShopOperationInfoVO.builder()
        .operationDate(shopOperationInfoDto.getOperationDate())
        .operationStatus(OperationStatus.find(shopOperationInfoDto, nowDateTime))
        .operationStartDateTime(shopOperationInfoDto.getOperationStartDateTime())
        .operationEndDateTime(shopOperationInfoDto.getOperationEndDateTime());

    if (StringUtils.hasText(manualPauseReason)) {
      shopOperationInfoVOBuilder
          .pauseStartDateTime(shopOperationInfoDto.getManualPauseStartDateTime())
          .pauseEndDateTime(shopOperationInfoDto.getManualPauseEndDateTime())
          .pauseReasonId(shopOperationInfoDto.getManualPauseReasonId())
          .pauseReason(manualPauseReason);
      return shopOperationInfoVOBuilder.build();
    }

    if (StringUtils.hasText(autoPauseReason)) {
      shopOperationInfoVOBuilder
          .pauseStartDateTime(shopOperationInfoDto.getAutoPauseStartDateTime())
          .pauseEndDateTime(shopOperationInfoDto.getAutoPauseEndDateTime())
          .pauseReasonId(shopOperationInfoDto.getAutoPauseReasonId())
          .pauseReason(autoPauseReason);
      return shopOperationInfoVOBuilder.build();
    }

    return shopOperationInfoVOBuilder.build();
  }

}
