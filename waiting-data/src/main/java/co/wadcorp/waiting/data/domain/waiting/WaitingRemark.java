package co.wadcorp.waiting.data.domain.waiting;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum WaitingRemark {


  PUT_OFF("미루기", WaitingDetailStatus.PUT_OFF),
  UNDO("되돌리기", WaitingDetailStatus.UNDO),
  UNDO_BY_CUSTOMER("되돌리기", WaitingDetailStatus.UNDO_BY_CUSTOMER),
  UNKNOWN("", null);


  private final String value;
  private final WaitingDetailStatus waitingDetailStatus;

  WaitingRemark(String value, WaitingDetailStatus waitingDetailStatus) {
    this.value = value;
    this.waitingDetailStatus = waitingDetailStatus;
  }

  public static WaitingRemark find(WaitingDetailStatus waitingDetailStatus) {
    return Arrays.stream(values())
        .filter(remark -> waitingDetailStatus == remark.waitingDetailStatus)
        .findFirst()
        .orElse(UNKNOWN);
  }

  public String getValue() {
    return value;
  }
}
