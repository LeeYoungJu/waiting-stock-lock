package co.wadcorp.waiting.data.query.waiting.dto;

import co.wadcorp.waiting.data.domain.waiting.WaitingDetailStatus;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitingHistoryDetailStatusDto {

  private Long seq;
  private String waitingId;
  private WaitingStatus waitingStatus;
  private WaitingDetailStatus waitingDetailStatus;

  public boolean isUndo() {
    return this.waitingDetailStatus == WaitingDetailStatus.UNDO;
  }

  public boolean isUndoByCustomer() {
    return this.waitingDetailStatus == WaitingDetailStatus.UNDO_BY_CUSTOMER;
  }

  public boolean isSitting() {
    return this.waitingStatus == WaitingStatus.SITTING;
  }

  public boolean isCancel() {
    return this.waitingStatus == WaitingStatus.CANCEL;
  }

  public boolean isCancelByNoShow() {
    return this.waitingDetailStatus == WaitingDetailStatus.CANCEL_BY_NO_SHOW;
  }

}
