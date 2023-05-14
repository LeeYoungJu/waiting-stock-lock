package co.wadcorp.waiting.api.service.waiting.management.dto.request;

import co.wadcorp.waiting.data.domain.memo.WaitingMemoEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WaitingMemoSaveServiceRequest {

  private String waitingId;

  private String memo;

  public WaitingMemoEntity toEntity(String shopId) {
    return WaitingMemoEntity.builder()
        .shopId(shopId)
        .waitingId(waitingId)
        .memo(memo)
        .build();
  }

}
