package co.wadcorp.waiting.api.internal.service.waiting.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RemoteWaitingListServiceRequest {

  private List<String> waitingIds;
  private LocalDate operationDate;

  @Builder
  private RemoteWaitingListServiceRequest(List<String> waitingIds, LocalDate operationDate) {
    this.waitingIds = waitingIds;
    this.operationDate = operationDate;
  }

}
