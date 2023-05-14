package co.wadcorp.waiting.api.internal.controller.table.dto.request;

import co.wadcorp.waiting.api.internal.service.table.dto.request.RemoteTableSettingBulkServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteTableSettingBulkRequest {

  @NotNull(message = "최소 Seq는 필수입니다.")
  private Long minSeq;

  @NotNull(message = "페이징 개수는 필수입니다.")
  private Integer size;

  @Builder
  private RemoteTableSettingBulkRequest(Long minSeq, Integer size) {
    this.minSeq = minSeq;
    this.size = size;
  }

  public RemoteTableSettingBulkServiceRequest toServiceRequest() {
    return RemoteTableSettingBulkServiceRequest.builder()
        .minSeq(minSeq)
        .size(size)
        .build();
  }

}
