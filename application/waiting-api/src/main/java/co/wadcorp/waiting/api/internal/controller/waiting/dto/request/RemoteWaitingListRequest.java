package co.wadcorp.waiting.api.internal.controller.waiting.dto.request;

import co.wadcorp.waiting.api.internal.service.waiting.dto.request.RemoteWaitingListServiceRequest;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RemoteWaitingListRequest {

  @NotEmpty(message = "웨이팅 ID 리스트는 필수입니다.")
  private List<String> waitingIds;

  private String operationDate;

  @Builder
  private RemoteWaitingListRequest(List<String> waitingIds, String operationDate) {
    this.waitingIds = waitingIds;
    this.operationDate = operationDate;
  }

  public RemoteWaitingListServiceRequest toServiceRequest(LocalDate operationDateFromNow) {
    LocalDate date = this.operationDate != null
        ? LocalDateUtils.parseToLocalDate(this.operationDate)
        : operationDateFromNow;

    return RemoteWaitingListServiceRequest.builder()
        .waitingIds(waitingIds)
        .operationDate(date)
        .build();
  }

}
