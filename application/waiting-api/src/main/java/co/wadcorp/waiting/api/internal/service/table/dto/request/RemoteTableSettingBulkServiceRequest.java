package co.wadcorp.waiting.api.internal.service.table.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteTableSettingBulkServiceRequest {

  private final Long minSeq;
  private final Integer size;

  @Builder
  private RemoteTableSettingBulkServiceRequest(Long minSeq, Integer size) {
    this.minSeq = minSeq;
    this.size = size;
  }

}
