package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.waiting.api.model.waiting.vo.WaitingVO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingResponse {

  @JsonUnwrapped
  private final WaitingVO waiting;

  @Builder
  public WaitingResponse(WaitingVO waiting) {
    this.waiting = waiting;
  }

}
