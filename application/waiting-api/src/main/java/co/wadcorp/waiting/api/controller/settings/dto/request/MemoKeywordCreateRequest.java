package co.wadcorp.waiting.api.controller.settings.dto.request;

import co.wadcorp.waiting.api.service.settings.dto.request.MemoKeywordMergeServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoKeywordCreateRequest {

  @NotBlank(message = "메모 키워드 ID는 필수입니다.")
  private String id;

  @NotBlank(message = "메모 키워드 내용은 필수입니다.")
  @Size(max = 20, message = "키워드 최대 길이는 20자입니다.")
  private String keyword;

  public MemoKeywordMergeServiceRequest toServiceRequest() {
    return MemoKeywordMergeServiceRequest.builder()
        .id(this.id)
        .keyword(this.keyword)
        .build();
  }

  @Builder
  public MemoKeywordCreateRequest(String id, String keyword) {
    this.id = id;
    this.keyword = keyword;
  }
}
