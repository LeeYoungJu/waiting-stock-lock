package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.waiting.DefaultWaitingReadyToEnterCondition;
import co.wadcorp.waiting.data.domain.waiting.PutOffWaitingReadyToEnterCondition;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.domain.waiting.WaitingReadyToEnterCondition;
import co.wadcorp.waiting.data.domain.waiting.WaitingStatus;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.CanceledByOutOfStockEvent;
import co.wadcorp.waiting.data.event.CanceledByShopEvent;
import co.wadcorp.waiting.data.event.NoShowedEvent;
import co.wadcorp.waiting.data.event.PutOffEvent;
import co.wadcorp.waiting.data.event.ReadyToEnterEvent;
import co.wadcorp.waiting.data.event.SeatedEvent;
import co.wadcorp.waiting.data.service.settings.AlarmSettingsService;
import co.wadcorp.waiting.data.service.waiting.WaitingManagementService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class WaitingReadyToEnterTargetEventHandler {

  private final WaitingService waitingService;
  private final WaitingManagementService waitingManagementService;
  private final WaitingHistoryService waitingHistoryService;
  private final AlarmSettingsService alarmSettingsService;

  private final DefaultWaitingReadyToEnterCondition defaultWaitingReadyToEnterCondition;
  private final PutOffWaitingReadyToEnterCondition putOffWaitingReadyToEnterCondition;

  private final ApplicationEventPublisher eventPublisher;

  @Async
  @TransactionalEventListener(PutOffEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void readyToEnterByPutOff(PutOffEvent event) {
    readyToEnter(event.shopId(), event.waitingHistorySeq(), event.operationDate(), putOffWaitingReadyToEnterCondition);
  }

  @Async
  @TransactionalEventListener(SeatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void readyToEnterBySeated(SeatedEvent event) {
    readyToEnter(event.shopId(), event.waitingHistorySeq(), event.operationDate(), defaultWaitingReadyToEnterCondition);
  }

  @Async
  @TransactionalEventListener(CanceledByShopEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void readyToEnterByCanceledByShop(CanceledByShopEvent event) {
    readyToEnter(event.shopId(), event.waitingHistorySeq(), event.operationDate(), defaultWaitingReadyToEnterCondition);
  }

  @Async
  @TransactionalEventListener(CanceledByCustomerEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void readyToEnterByCancelByCustomer(CanceledByCustomerEvent event) {
    readyToEnter(event.shopId(), event.waitingHistorySeq(), event.operationDate(), defaultWaitingReadyToEnterCondition);
  }

  @Async
  @TransactionalEventListener(NoShowedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void readyToEnterByNoShow(NoShowedEvent event) {
    readyToEnter(event.shopId(), event.waitingHistorySeq(), event.operationDate(), defaultWaitingReadyToEnterCondition);
  }

  @Async
  @TransactionalEventListener(CanceledByOutOfStockEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void readyToEnterByCancelByOutOfStock(CanceledByOutOfStockEvent event) {
    readyToEnter(event.shopId(), event.waitingHistorySeq(), event.operationDate(), defaultWaitingReadyToEnterCondition);
  }

  private void readyToEnter(String shopId, Long waitingHistorySeq, LocalDate operationDate,
      WaitingReadyToEnterCondition waitingReadyToEnterCondition) {

    AlarmSettingsEntity alarmSettings = alarmSettingsService.getAlarmSettings(shopId);
    if (!alarmSettings.isUsedAutoEnterAlarm()) {
      return;
    }

    Integer autoAlarmOrdering = alarmSettings.getAutoAlarmOrdering();
    WaitingHistoryEntity eventWaitingHistory = waitingHistoryService.findById(waitingHistorySeq);

    List<WaitingEntity> sameSeatedWaitings = findSameSeatedWaitings(shopId, operationDate,
        eventWaitingHistory);

    if (isLessThenAutoAlarmOrdering(sameSeatedWaitings, autoAlarmOrdering)) {
      return;
    }

    WaitingEntity targetWaiting = sameSeatedWaitings.get(autoAlarmOrdering - 1);
    if (waitingReadyToEnterCondition.canReadyToEnter(eventWaitingHistory, targetWaiting)) {
      readyToEnter(shopId, operationDate, targetWaiting);
    }

  }

  private void readyToEnter(String shopId, LocalDate operationDate, WaitingEntity targetWaiting) {
    WaitingHistoryEntity waitingHistory = waitingManagementService.readyToEnter(shopId,
        targetWaiting.getWaitingId(), operationDate);

    // 입장 임박 알림톡 전송
    eventPublisher.publishEvent(
        new ReadyToEnterEvent(shopId, waitingHistory.getSeq(), operationDate)
    );
  }

  private List<WaitingEntity> findSameSeatedWaitings(String shopId, LocalDate operationDate,
      WaitingHistoryEntity eventWaitingHistory) {
    List<WaitingEntity> waitingEntities = waitingService.findAllByShopIdAndOperationDateAndWaitingStatus(
        shopId, operationDate, WaitingStatus.WAITING);

    return waitingEntities.stream()
        .filter(waitingEntity ->
            waitingEntity.isSameSeatOptionName(eventWaitingHistory.getSeatOptionName())
        )
        .sorted(Comparator.comparing(WaitingEntity::getWaitingOrder))
        .toList();
  }

  private boolean isLessThenAutoAlarmOrdering(List<WaitingEntity> waitingEntities,
      Integer autoAlarmOrdering) {
    return waitingEntities.size() < autoAlarmOrdering;
  }

}
