package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.memo.MemoKeywordOrderingUpdateDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemoKeywordOrderingServiceRequest {

  private List<KeywordOrderingServiceDto> orderedKeywords;

  @Builder
  private MemoKeywordOrderingServiceRequest(List<KeywordOrderingServiceDto> orderedKeywords) {
    this.orderedKeywords = orderedKeywords;
  }

  public MemoKeywordOrderingUpdateDto toDataDto() {
    return MemoKeywordOrderingUpdateDto.builder()
        .orderingDataList(orderedKeywords.stream()
            .map(keyword -> MemoKeywordOrderingUpdateDto.MemoKeywordOrderingData.builder()
                .keywordId(keyword.id)
                .ordering(keyword.ordering)
                .build())
            .toList()
        ).build();
  }

  @Getter
  public static class KeywordOrderingServiceDto {
    private String id;
    private int ordering;

    @Builder
    private KeywordOrderingServiceDto(String id, int ordering) {
      this.id = id;
      this.ordering = ordering;
    }
  }
}
