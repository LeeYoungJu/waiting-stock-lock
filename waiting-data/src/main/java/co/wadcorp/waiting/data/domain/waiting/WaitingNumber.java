package co.wadcorp.waiting.data.domain.waiting;

import co.wadcorp.waiting.data.config.WaitingNumberConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class WaitingNumber {

  @Column(name = "waiting_number")
  private Integer waitingNumber; // 채번. 고객이 볼 수 있는 번호 (ex. 101번)

  @Column(name = "waiting_order")
  private Integer waitingOrder; // 순번. 1부터 시작하는 번호. 등록한 순서. 마지막 순번 + 1

  public static WaitingNumber ofDefault() {
    return WaitingNumber.builder()
        .waitingNumber(WaitingNumberConstructor.initWaitingNumber())
        .waitingOrder(1)
        .build();
  }

  public void putOffWaitingOrder(int maxWaitingOrder) {
    this.waitingOrder = maxWaitingOrder;
  }

  public boolean isWaitingOrderLessThan(Integer waitingOrder) {
    return this.waitingOrder < waitingOrder;
  }

  public boolean isWaitingOrderGreaterOrEqualThan(int waitingOrder) {
    return this.waitingOrder >= waitingOrder;
  }

  public boolean isSameWaitingOrder(Integer waitingOrder) {
    return this.waitingOrder.equals(waitingOrder);
  }

}
