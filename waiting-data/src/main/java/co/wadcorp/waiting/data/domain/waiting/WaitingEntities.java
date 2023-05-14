package co.wadcorp.waiting.data.domain.waiting;

import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;

public record WaitingEntities(List<WaitingEntity> entities) {

  public WaitingNumber createWaitingNumber() {
    if (ObjectUtils.isEmpty(entities)) {
      return WaitingNumber.ofDefault();
    }

    return WaitingNumber.builder()
        .waitingNumber(getMaxWaitingNumber() + 1)
        .waitingOrder(getMaxWaitingOrder() + 1)
        .build();
  }

  public int getRegisterWaitingOrder(String seatOptionName) {
    return (int) entities.stream()
        .filter(waiting -> waiting.isWaitingStatus()
            && seatOptionName.equals(waiting.getSeatOptionName()))
        .count();
  }

  private Integer getMaxWaitingNumber() {
    return entities.stream()
        .max(Comparator.comparing(WaitingEntity::getWaitingNumber))
        .map(WaitingEntity::getWaitingNumber)
        .orElse(0);
  }

  private Integer getMaxWaitingOrder() {
    return entities.stream()
        .max(Comparator.comparing(WaitingEntity::getWaitingOrder))
        .map(WaitingEntity::getWaitingOrder)
        .orElse(0);
  }

}
