package co.wadcorp.waiting.data.domain.waiting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DefaultWaitingReadyToEnterCondition implements WaitingReadyToEnterCondition {

  private final WaitingHistoryService waitingHistoryService;

  @Override
  public boolean canReadyToEnter(WaitingHistoryEntity eventWaitingHistory,
      WaitingEntity targetWaiting
  ) {

    if (isEventWaitingAfterThanAlarmTarget(eventWaitingHistory.getWaitingOrder(), targetWaiting)) {
      return false;
    }

    return !isTargetWaitingAlreadyChangedStatus(targetWaiting.getWaitingId());
  }

  private boolean isEventWaitingAfterThanAlarmTarget(Integer waitingOrder,
      WaitingEntity waiting) {
    return waiting.isWaitingOrderLessThan(waitingOrder);
  }

  private boolean isTargetWaitingAlreadyChangedStatus(String waitingId) {
    return waitingHistoryService.existsByWaitingIdAndWaitingDetailStatus(waitingId,
        WaitingDetailStatus.READY_TO_ENTER);
  }

}
