package co.wadcorp.waiting.api.model.waiting.request;

import lombok.Getter;

@Getter
public class PageRequestParams {

  private int page = 1;
  private int limit = 100;

  public PageRequestParams() {
  }

  public PageRequestParams(int page, int limit) {
    this.page = page;
    this.limit = limit;
  }

  public int pageByPageRequest() {
    if(page < 1) {
      return 0;
    }
    return page - 1;
  }
}
