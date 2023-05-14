package co.wadcorp.waiting.api.service.settings.dto.request;

import co.wadcorp.waiting.data.domain.memo.MemoKeywordEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoKeywordMergeServiceRequest {

  private String id;
  private String keyword;

  public MemoKeywordEntity toEntity(String shopId, int ordering) {
    return MemoKeywordEntity.builder()
        .keywordId(this.id)
        .shopId(shopId)
        .keyword(this.keyword)
        .ordering(ordering)
        .build();
  }
}
