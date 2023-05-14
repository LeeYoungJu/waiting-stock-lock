package co.wadcorp.waiting.api.service.settings.dto.response;

import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoKeywordResponse {

  private String id;
  private String keyword;
  private int ordering;

  public static MemoKeywordResponse of(MemoKeywordEntity entity) {
    return MemoKeywordResponse.builder()
        .id(entity.getKeywordId())
        .keyword(entity.getKeyword())
        .ordering(entity.getOrdering())
        .build();
  }

}
