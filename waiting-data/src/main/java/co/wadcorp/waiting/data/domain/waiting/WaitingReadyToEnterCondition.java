package co.wadcorp.waiting.data.domain.waiting;

public interface WaitingReadyToEnterCondition {

  boolean canReadyToEnter(WaitingHistoryEntity eventWaitingHistory, WaitingEntity targetWaiting);
}
