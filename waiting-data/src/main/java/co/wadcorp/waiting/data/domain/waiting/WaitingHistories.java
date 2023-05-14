package co.wadcorp.waiting.data.domain.waiting;

import java.util.List;
import lombok.Getter;

@Getter
public class WaitingHistories {

  private static final int CAN_PUT_OFF_COUNT = 2;
  private static final int CAN_CALL_COUNT = 2;

  private final List<WaitingHistoryEntity> histories;

  public WaitingHistories(List<WaitingHistoryEntity> histories) {
    this.histories = histories;
  }

  public boolean canCall() {
    return histories.stream()
        .filter(history -> history.getWaitingDetailStatus() == WaitingDetailStatus.CALL)
        .count() < CAN_CALL_COUNT;
  }

  public boolean canReadyToEnter() {
    return histories.stream()
        .noneMatch(history -> history.getWaitingDetailStatus() == WaitingDetailStatus.READY_TO_ENTER);
  }

  public boolean anyMatchCall() {
    return this.histories.stream()
        .anyMatch(item -> item.getWaitingDetailStatus() == WaitingDetailStatus.CALL);
  }

  public boolean canPutOffCount() {
    return getPutOffCount() < CAN_PUT_OFF_COUNT;
  }

  public long getPutOffCount() {
    return this.histories.stream()
        .filter(item -> item.getWaitingDetailStatus() == WaitingDetailStatus.PUT_OFF)
        .count();
  }

}
