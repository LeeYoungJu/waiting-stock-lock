package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.event.CalledEvent;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.CanceledByOutOfStockEvent;
import co.wadcorp.waiting.data.event.CanceledByShopEvent;
import co.wadcorp.waiting.data.event.NoShowedEvent;
import co.wadcorp.waiting.data.event.PutOffEvent;
import co.wadcorp.waiting.data.event.RegisteredEvent;
import co.wadcorp.waiting.data.event.SeatCanceledEvent;
import co.wadcorp.waiting.data.event.SeatedEvent;
import co.wadcorp.waiting.data.event.UndoEvent;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingPublisherV2;
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
public class ChangedWaitingKafkaPublishEventHandler {

  private final WaitingHistoryService waitingHistoryService;

  private final WaitingPublisherV2 waitingPublisherV2;

  @Async
  @TransactionalEventListener(RegisteredEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishRegistered(RegisteredEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("REGISTERED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(CalledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishSeated(CalledEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("CALLED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(SeatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishSeated(SeatedEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("SEATED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(SeatCanceledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByShop(SeatCanceledEvent event) {

    List<WaitingHistoryEntity> waitingHistoryEntities = waitingHistoryService.findAllBySeqIn(
        event.canceledWaitingHistorySeq());

    waitingHistoryEntities.forEach(waitingHistory -> {
      publish("CANCELED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());
    });

  }

  @Async
  @TransactionalEventListener(CanceledByCustomerEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByCustomer(CanceledByCustomerEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("CANCELED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());

  }

  @Async
  @TransactionalEventListener(CanceledByShopEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByShop(CanceledByShopEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("CANCELED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());

  }

  @Async
  @TransactionalEventListener(NoShowedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishNoShow(NoShowedEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("CANCELED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(CanceledByOutOfStockEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByOutOfStockEvent(CanceledByOutOfStockEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("CANCELED", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());

  }

  @Async
  @TransactionalEventListener(UndoEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishUndo(UndoEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("UNDO", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());
  }


  @Async
  @TransactionalEventListener(PutOffEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishUndo(PutOffEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    publish("PUT_OFF", waitingHistory.getShopId(), waitingHistory.getWaitingId(), event.deviceId());
  }

  private void publish(String type, String shopId, String waitingId, String deviceId) {
    log.info("웨이팅 상태 변경 이벤트 shopId={}, waitingId={}, type={}", shopId, waitingId, type);
    waitingPublisherV2.publish(type, shopId, waitingId, deviceId);
  }
}
