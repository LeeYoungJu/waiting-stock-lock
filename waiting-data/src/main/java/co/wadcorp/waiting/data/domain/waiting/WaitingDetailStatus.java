package co.wadcorp.waiting.data.domain.waiting;

import lombok.Getter;

@Getter
public enum WaitingDetailStatus {


  /**
   * WAITING
   */
  WAITING("웨이팅 등록", WaitingStatus.WAITING),
  PUT_OFF("미루기", WaitingStatus.WAITING),
  CALL("입장호출", WaitingStatus.WAITING),
  READY_TO_ENTER("입장준비", WaitingStatus.WAITING),
  DELAY("입장지연", WaitingStatus.WAITING),
  UNDO("웨이팅복귀 - 관리자요청", WaitingStatus.WAITING),
  UNDO_BY_CUSTOMER("웨이팅복귀 - 고객요청", WaitingStatus.WAITING),

  /**
   * SITTING
   */
  SITTING("착석완료", WaitingStatus.SITTING),

  /**
   * CANCEL
   */
  CANCEL_BY_CUSTOMER("웨이팅취소 - 고객요청", WaitingStatus.CANCEL),
  CANCEL_BY_SITTING("웨이팅취소 - 타매장방문", WaitingStatus.CANCEL),
  CANCEL_BY_SHOP("웨이팅취소 - 매장요청", WaitingStatus.CANCEL),
  CANCEL_BY_NO_SHOW("웨이팅취소 - 고객미방문(노쇼)", WaitingStatus.CANCEL),
  CANCEL_BY_OUT_OF_STOCK("웨이팅취소 - 재고소진", WaitingStatus.CANCEL),


  /**
   * EXPIRATION
   */
  EXPIRATION("만료", WaitingStatus.EXPIRATION),
  ;

  private final String value;
  private final WaitingStatus waitingStatus;

  WaitingDetailStatus(String value, WaitingStatus waitingStatus) {
    this.value = value;
    this.waitingStatus = waitingStatus;
  }

  public WaitingStatus getWaitingStatus() {
    return waitingStatus;
  }
}
