package co.wadcorp.waiting.api.model.waiting.vo;

import lombok.Getter;

@Getter
public class PageVO {

  private final long totalCount;
  private final long page;
  private final long limit;

  public PageVO(long totalCount, long page, long limit) {
    this.totalCount = totalCount;
    this.page = page;
    this.limit = limit;
  }
}
