package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetRepository;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.CanceledByOutOfStockEvent;
import co.wadcorp.waiting.data.event.CanceledByShopEvent;
import co.wadcorp.waiting.data.event.NoShowedEvent;
import co.wadcorp.waiting.data.event.SeatCanceledEvent;
import co.wadcorp.waiting.data.event.SeatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 2회 호출 당하면 자동 취소 대상으로 선별되는데, (WaitingAutoCancelByCalledEventHandler)
 * <p>
 * 자동 취소 실행 시점에 웨이팅이 착석되거나 취소 상태가 되면 대상에서 삭제해야 한다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WaitingAutoCancelTargetIgnoreEventHandler {

  private final WaitingHistoryService waitingHistoryService;
  private final AutoCancelTargetRepository autoCancelTargetRepository;

  @Async
  @TransactionalEventListener(SeatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void ignoreTargetBySeated(SeatedEvent event) {
    checkAutoCancelTarget(event.waitingHistorySeq());
  }

  @Async
  @TransactionalEventListener(CanceledByShopEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void ignoreTargetByCancelByShop(CanceledByShopEvent event) {
    checkAutoCancelTarget(event.waitingHistorySeq());
  }

  @Async
  @TransactionalEventListener(CanceledByCustomerEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void ignoreTargetByCanceledByCustomer(CanceledByCustomerEvent event) {
    checkAutoCancelTarget(event.waitingHistorySeq());
  }

  @Async
  @TransactionalEventListener(NoShowedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void ignoreTargetBySeated(NoShowedEvent event) {
    checkAutoCancelTarget(event.waitingHistorySeq());
  }

  @Async
  @TransactionalEventListener(CanceledByOutOfStockEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void ignoreTargetByCanceledByOutOfStockEvent(CanceledByOutOfStockEvent event) {
    checkAutoCancelTarget(event.waitingHistorySeq());
  }

  @Async
  @TransactionalEventListener(SeatCanceledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void ignoreTargetBySeatCanceled(SeatCanceledEvent event) {
    checkAutoCancelTarget(event.waitingHistorySeq());

    event.canceledWaitingHistorySeq()
        .forEach(this::checkAutoCancelTarget);
  }

  private void checkAutoCancelTarget(Long historySeq) {
    WaitingHistoryEntity history = waitingHistoryService.findById(historySeq);
    String waitingId = history.getWaitingId();

    autoCancelTargetRepository.findByWaitingId(waitingId)
        .ifPresent(AutoCancelTargetEntity::ignore);
  }

}
