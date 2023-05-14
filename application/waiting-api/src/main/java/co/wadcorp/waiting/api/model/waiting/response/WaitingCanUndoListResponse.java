package co.wadcorp.waiting.api.model.waiting.response;

import co.wadcorp.libs.datetime.ISO8601;
import co.wadcorp.waiting.api.model.waiting.vo.CanUndoWaitingVO;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WaitingCanUndoListResponse {

  public static final WaitingCanUndoListResponse EMPTY_RESULT = WaitingCanUndoListResponse.builder()
      .waitingList(List.of())
      .build();

  private final String canUndoDateTime;
  private final List<CanUndoWaitingVO> waitingList;

  @Builder
  public WaitingCanUndoListResponse(ZonedDateTime canUndoDateTime, List<CanUndoWaitingVO> waitingList) {
    this.canUndoDateTime = ISO8601.format(canUndoDateTime);
    this.waitingList = waitingList;
  }
}
