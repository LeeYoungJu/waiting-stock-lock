package co.wadcorp.waiting.api.controller.settings.dto.request;

import static co.wadcorp.waiting.api.service.settings.dto.request.MemoKeywordOrderingServiceRequest.KeywordOrderingServiceDto;

import co.wadcorp.waiting.api.controller.support.NonDuplicateOrderingConstraint;
import co.wadcorp.waiting.api.controller.support.Ordering;
import co.wadcorp.waiting.api.service.settings.dto.request.MemoKeywordOrderingServiceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoKeywordOrderingRequest {

  @NotEmpty(message = "메모 키워드 리스트는 필수입니다.")
  @NonDuplicateOrderingConstraint
  @Valid
  private List<KeywordOrderingDto> keywords;

  @Builder
  private MemoKeywordOrderingRequest(List<KeywordOrderingDto> keywords) {
    this.keywords = keywords;
  }

  public MemoKeywordOrderingServiceRequest toServiceRequest() {
    return MemoKeywordOrderingServiceRequest.builder()
        .orderedKeywords(keywords.stream()
            .map(keyword -> KeywordOrderingServiceDto.builder()
                .id(keyword.id)
                .ordering(keyword.ordering)
                .build())
            .toList())
        .build();
  }

  @Getter
  @NoArgsConstructor
  public static class KeywordOrderingDto implements Ordering {

    @NotBlank(message = "메모 키워드 ID는 필수입니다.")
    private String id;

    @Min(value = 1, message = "메모 키워드 순서값은 1이상이어야 합니다.")
    private int ordering;

    @Builder
    private KeywordOrderingDto(String id, int ordering) {
      this.id = id;
      this.ordering = ordering;
    }
  }
}
