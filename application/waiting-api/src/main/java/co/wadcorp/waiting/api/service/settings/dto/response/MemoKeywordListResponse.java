package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoKeywordListResponse {

  private List<MemoKeywordDto> keywords;

  public static MemoKeywordListResponse of(List<MemoKeywordEntity> entities) {
    return MemoKeywordListResponse.builder()
        .keywords(entities.stream()
            .sorted()
            .map(MemoKeywordDto::of)
            .toList())
        .build();
  }

  @Getter
  @Builder
  public static class MemoKeywordDto {
    private String id;
    private String keyword;
    private int ordering;

    public static MemoKeywordDto of(MemoKeywordEntity entity) {
      return MemoKeywordDto.builder()
          .id(entity.getKeywordId())
          .keyword(entity.getKeyword())
          .ordering(entity.getOrdering())
          .build();
    }
  }

}
