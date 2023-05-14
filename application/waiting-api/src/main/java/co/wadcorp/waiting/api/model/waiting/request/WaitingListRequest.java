package co.wadcorp.waiting.api.model.waiting.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WaitingListRequest {

  private String modeType = "DEFAULT";
  private List<String> seatOptionId;
  private String waitingStatus = "WAITING";
  private int page = 1;
  private int limit = 100;

  public WaitingListRequest() {
  }

  public WaitingListRequest(String modeType, List<String> seatOptionId, String waitingStatus,
      int page, int limit) {
    this.modeType = modeType;
    this.seatOptionId = seatOptionId;
    this.waitingStatus = waitingStatus;
    this.page = page;
    this.limit = limit;
  }

  @JsonIgnore
  public boolean isTableMode() {
    return "TABLE".equals(modeType);
  }

  public int pageByPageRequest() {
    if(page < 1) {
      return 0;
    }
    return page - 1;
  }
}
