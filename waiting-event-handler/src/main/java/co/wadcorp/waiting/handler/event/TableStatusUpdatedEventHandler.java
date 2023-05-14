package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.channel.ChannelMappingEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.enums.WaitingModeType;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.CanceledByOutOfStockEvent;
import co.wadcorp.waiting.data.event.CanceledByShopEvent;
import co.wadcorp.waiting.data.event.NoShowedEvent;
import co.wadcorp.waiting.data.event.RegisteredEvent;
import co.wadcorp.waiting.data.event.SeatCanceledEvent;
import co.wadcorp.waiting.data.event.SeatedEvent;
import co.wadcorp.waiting.data.event.TableStatusUpdateEvent;
import co.wadcorp.waiting.data.event.TableUpdatedEvent;
import co.wadcorp.waiting.data.event.UndoEvent;
import co.wadcorp.waiting.data.query.settings.HomeSettingsQueryRepository;
import co.wadcorp.waiting.data.service.channel.SelectChannelService;
import co.wadcorp.waiting.data.service.waiting.TableCurrentStatusService;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingTableCurrentStatusPublisher;
import co.wadcorp.waiting.shared.enums.ServiceChannelId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
public class TableStatusUpdatedEventHandler {

  private final SelectChannelService selectChannelService;
  private final WaitingHistoryService waitingHistoryService;
  private final WaitingTableCurrentStatusPublisher waitingTableCurrentStatusPublisher;
  private final HomeSettingsQueryRepository homeSettingsQueryRepository;
  private final TableCurrentStatusService tableCurrentStatusService;
  private final ApplicationEventPublisher eventPublisher;

  @Async
  @TransactionalEventListener(RegisteredEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishRegistered(RegisteredEvent event) {

    update(event.shopId(), event.operationDate());
  }

  @Async
  @TransactionalEventListener(SeatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishSeated(SeatedEvent event) {

    update(event.shopId(), event.operationDate());
  }

  @Async
  @TransactionalEventListener(SeatCanceledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByShop(SeatCanceledEvent event) {

    List<WaitingHistoryEntity> waitingHistoryEntities = waitingHistoryService.findAllBySeqIn(
        event.canceledWaitingHistorySeq());

    waitingHistoryEntities.forEach(
        item -> update(item.getShopId(), event.operationDate())
    );
  }

  @Async
  @TransactionalEventListener(CanceledByCustomerEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByCustomer(CanceledByCustomerEvent event) {

    update(event.shopId(), event.operationDate());
  }

  @Async
  @TransactionalEventListener(CanceledByShopEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByShop(CanceledByShopEvent event) {

    update(event.shopId(), event.operationDate());
  }

  @Async
  @TransactionalEventListener(NoShowedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishNoShow(NoShowedEvent event) {

    update(event.shopId(), event.operationDate());
  }

  @Async
  @TransactionalEventListener(CanceledByOutOfStockEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishCanceledByOutOfStockEvent(CanceledByOutOfStockEvent event) {

    update(event.shopId(), event.operationDate());
  }

  @Async
  @TransactionalEventListener(UndoEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishUndo(UndoEvent event) {

    update(event.shopId(), event.operationDate());
  }


  @Async
  @TransactionalEventListener(TableUpdatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishTableUpdated(TableUpdatedEvent event) {

    update(event.shopId(), event.operationDate());
  }


  private void update(String shopId, LocalDate operationDate) {
    WaitingModeType modeType = getModeType(shopId);
    tableCurrentStatusService.update(shopId, operationDate, modeType);

    eventPublisher.publishEvent(new TableStatusUpdateEvent(shopId, operationDate));
  }

  private WaitingModeType getModeType(String shopId) {
    HomeSettingsEntity homeSettings = homeSettingsQueryRepository.findByShopId(shopId);
    HomeSettingsData homeSettingsData = homeSettings.getHomeSettingsData();

    if (homeSettingsData.isDefaultMode()) {
      return WaitingModeType.DEFAULT;
    }
    return WaitingModeType.TABLE;
  }

  @Async
  @TransactionalEventListener(TableStatusUpdateEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishNoShow(TableStatusUpdateEvent event) {
    publish(event.shopId());
  }

  private void publish(String shopId) {
    Optional<ChannelMappingEntity> channelMappingByWaitingShopIds = selectChannelService.getChannelMappingByWaitingShopIds(
        ServiceChannelId.CATCHTABLE_B2C.getValue(),
        shopId);

    channelMappingByWaitingShopIds.ifPresent((item) -> {
      Long shopSeq = Long.valueOf(item.getChannelShopId());
      log.info("테이블 현황 변경 이벤트 shopSeq={}", shopSeq);
      waitingTableCurrentStatusPublisher.publish(shopSeq);
    });
  }

}
