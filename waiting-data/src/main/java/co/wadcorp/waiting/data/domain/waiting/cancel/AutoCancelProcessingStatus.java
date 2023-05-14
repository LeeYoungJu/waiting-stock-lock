package co.wadcorp.waiting.data.domain.waiting.cancel;

import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AutoCancelProcessingStatus {

  CREATED("신규 생성"),
  SUCCESS("성공"),
  IGNORE("대상 아님 - 대상 확정 후 착석하거나 취소한 경우");

  private static final Set<AutoCancelProcessingStatus> COMPLETED_STATUSES = Set.of(SUCCESS, IGNORE);

  private final String text;

  public boolean isCompletedStatus() {
    return COMPLETED_STATUSES.contains(this);
  }

}
