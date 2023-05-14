package co.wadcorp.waiting.api.internal.service.shop.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteShopBulkServiceRequest {

  private final Long minSeq;
  private final Integer size;

  @Builder
  private RemoteShopBulkServiceRequest(Long minSeq, Integer size) {
    this.minSeq = minSeq;
    this.size = size;
  }

}
