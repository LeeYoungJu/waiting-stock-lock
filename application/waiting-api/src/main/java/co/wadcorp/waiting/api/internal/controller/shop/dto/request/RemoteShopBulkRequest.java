package co.wadcorp.waiting.api.internal.controller.shop.dto.request;

import co.wadcorp.waiting.api.internal.service.shop.dto.request.RemoteShopBulkServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteShopBulkRequest {

  @NotNull(message = "최소 Seq는 필수입니다.")
  private Long minSeq;

  @NotNull(message = "페이징 개수는 필수입니다.")
  @Min(value = 1, message = "페이징 개수는 1 이상의 수입니다.")
  @Max(value = 500, message = "페이징 개수는 최대 500까지입니다.")
  private Integer size;

  @Builder
  private RemoteShopBulkRequest(Long minSeq, Integer size) {
    this.minSeq = minSeq;
    this.size = size;
  }

  public RemoteShopBulkServiceRequest toServiceRequest() {
    return RemoteShopBulkServiceRequest.builder()
        .minSeq(minSeq)
        .size(size)
        .build();
  }

}
