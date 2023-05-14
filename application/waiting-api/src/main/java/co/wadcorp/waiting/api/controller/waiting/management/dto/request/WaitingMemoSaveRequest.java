package co.wadcorp.waiting.api.controller.waiting.management.dto.request;

import co.wadcorp.waiting.api.service.waiting.management.dto.request.WaitingMemoSaveServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitingMemoSaveRequest {

  @NotBlank
  private String waitingId;

  @Size(max = 2000, message = "메모 최대 길이는 2000자 입니다.")
  @NotNull(message = "메모 내용은 null을 허용하지 않습니다.")
  private String memo;

  @Builder
  private WaitingMemoSaveRequest(String waitingId, String memo) {
    this.waitingId = waitingId;
    this.memo = memo;
  }

  public WaitingMemoSaveServiceRequest toServiceRequest() {
    return WaitingMemoSaveServiceRequest.builder()
        .waitingId(waitingId)
        .memo(memo)
        .build();
  }
}
