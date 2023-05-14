package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.event.CalledEvent;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.CanceledByOutOfStockEvent;
import co.wadcorp.waiting.data.event.CanceledByShopEvent;
import co.wadcorp.waiting.data.event.DelayedEvent;
import co.wadcorp.waiting.data.event.NoShowedEvent;
import co.wadcorp.waiting.data.event.PutOffEvent;
import co.wadcorp.waiting.data.event.ReadyToEnterEvent;
import co.wadcorp.waiting.data.event.RegisteredEvent;
import co.wadcorp.waiting.data.event.SeatCanceledEvent;
import co.wadcorp.waiting.data.event.SeatedEvent;
import co.wadcorp.waiting.data.event.UndoEvent;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingPublisher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class WaitingUpdatedEventHandler {

  private final WaitingHistoryService waitingHistoryService;
  private final WaitingService waitingService;

  private final WaitingPublisher waitingPublisher;

  @Async
  @TransactionalEventListener(RegisteredEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(RegisteredEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    publish(List.of(waitingHistory.getWaitingId()));
  }

  @Async
  @TransactionalEventListener(PutOffEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(PutOffEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());


    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            event.beforeWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(CanceledByCustomerEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(CanceledByCustomerEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(CanceledByShopEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(CanceledByShopEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(NoShowedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(NoShowedEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());
    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(CanceledByOutOfStockEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(CanceledByOutOfStockEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(UndoEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(UndoEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(DelayedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(DelayedEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(ReadyToEnterEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(ReadyToEnterEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }


  @Async
  @TransactionalEventListener(SeatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(SeatedEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }

  @Async
  @TransactionalEventListener(CalledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(CalledEvent event) {
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    List<String> waitingIds = waitingService
        .findAllByShopIdAndOperationDateAndSeatOptionNameAndWaitingOrderGreaterThanEqual(
            waitingHistory.getShopId(), waitingHistory.getOperationDate(),
            waitingHistory.getSeatOptionName(),
            waitingHistory.getWaitingOrder()
        );

    publish(waitingIds);
  }


  @Async
  @TransactionalEventListener(SeatCanceledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void event(SeatCanceledEvent event) {
    List<WaitingHistoryEntity> waitingHistoryEntities = waitingHistoryService.findAllBySeqIn(
        event.canceledWaitingHistorySeq());


    publish(waitingHistoryEntities.stream()
        .map(WaitingHistoryEntity::getWaitingId)
        .toList());
  }

  public void publish(List<String> waitingIds) {

    log.info("웨이팅 변경 이벤트 waitingIds={}", waitingIds);
    waitingPublisher.publish(waitingIds);
  }

}
