package co.wadcorp.waiting.data.domain.memo;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemoKeywordOrderingUpdateDto {

  private List<MemoKeywordOrderingData> orderingDataList;

  @Builder
  private MemoKeywordOrderingUpdateDto(List<MemoKeywordOrderingData> orderingDataList) {
    this.orderingDataList = orderingDataList;
  }

  @Getter
  public static class MemoKeywordOrderingData {
    private String keywordId;
    private int ordering;

    @Builder
    public MemoKeywordOrderingData(String keywordId, int ordering) {
      this.keywordId = keywordId;
      this.ordering = ordering;
    }
  }

}
