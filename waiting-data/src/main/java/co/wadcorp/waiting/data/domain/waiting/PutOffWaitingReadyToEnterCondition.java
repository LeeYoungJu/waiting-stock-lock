package co.wadcorp.waiting.data.domain.waiting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PutOffWaitingReadyToEnterCondition implements WaitingReadyToEnterCondition {

  private final WaitingHistoryService waitingHistoryService;

  @Override
  public boolean canReadyToEnter(WaitingHistoryEntity eventWaitingHistory,
      WaitingEntity targetWaiting
  ) {

    return !isTargetWaitingAlreadyChangedStatus(targetWaiting.getWaitingId());
  }

  private boolean isTargetWaitingAlreadyChangedStatus(String waitingId) {
    return waitingHistoryService.existsByWaitingIdAndWaitingDetailStatus(waitingId,
        WaitingDetailStatus.READY_TO_ENTER);
  }

}
