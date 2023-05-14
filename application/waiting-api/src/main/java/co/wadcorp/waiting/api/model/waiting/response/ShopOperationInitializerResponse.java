package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.data.domain.shop.operation.status.OperationStatus;
import co.wadcorp.waiting.data.domain.shop.operation.ShopOperationInfoEntity;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ShopOperationInitializerResponse {

  private final String shopId;
  private final String operationDate;
  private final String operationStatus;
  private final String operationStartDateTime;
  private final String operationEndDateTime;

  @Builder
  public ShopOperationInitializerResponse(String shopId, LocalDate operationDate,
      OperationStatus operationStatus, ZonedDateTime operationStartDateTime,
      ZonedDateTime operationEndDateTime) {
    this.shopId = shopId;
    this.operationDate = operationDate.toString();
    this.operationStatus = operationStatus.name();
    this.operationStartDateTime = ISO8601.format(operationStartDateTime);
    this.operationEndDateTime = ISO8601.format(operationEndDateTime);
  }

  public static ShopOperationInitializerResponse toDto(ShopOperationInfoEntity entity, ZonedDateTime nowDateTime) {
    return ShopOperationInitializerResponse.builder()
        .shopId(entity.getShopId())
        .operationDate(entity.getOperationDate())
        .operationStatus(
            OperationStatus.find(entity, nowDateTime))
        .operationStartDateTime(entity.getOperationStartDateTime())
        .operationEndDateTime(entity.getOperationEndDateTime())
        .build();
  }
}
