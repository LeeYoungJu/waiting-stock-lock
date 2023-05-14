package co.wadcorp.waiting.api.service.waiting.management.dto.response;

import co.wadcorp.waiting.data.domain.memo.WaitingMemoEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WaitingMemoSaveResponse {

  private String waitingId;

  private String memo;

  public static WaitingMemoSaveResponse of(WaitingMemoEntity entity) {
    return WaitingMemoSaveResponse.builder()
        .waitingId(entity.getWaitingId())
        .memo(entity.getMemo())
        .build();
  }
}
