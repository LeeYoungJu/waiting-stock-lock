package co.wadcorp.waiting.api.internal.service.meta.dto;

import co.wadcorp.waiting.api.internal.service.person.dto.response.RemotePersonOptionResponse;
import co.wadcorp.waiting.api.internal.service.shop.dto.response.RemoteShopOperationResponse;
import co.wadcorp.waiting.api.internal.service.table.dto.response.RemoteTableSettingResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteMetaResponse {

  private final Long shopId;
  private final RemoteShopOperationResponse shopOperation;
  private final RemoteTableSettingResponse tableSetting;
  private final RemotePersonOptionResponse personOption;

  @Builder
  public RemoteMetaResponse(Long shopId, RemoteShopOperationResponse shopOperation,
      RemoteTableSettingResponse tableSetting, RemotePersonOptionResponse personOption) {
    this.shopId = shopId;
    this.shopOperation = shopOperation;
    this.tableSetting = tableSetting;
    this.personOption = personOption;
  }
}
