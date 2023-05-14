package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.settings.HomeSettingsData;
import co.wadcorp.waiting.data.domain.settings.SeatOptions;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.domain.waiting.WaitingSeatCanceledHistoryEntity;
import co.wadcorp.waiting.data.event.SeatCanceledEvent;
import co.wadcorp.waiting.data.event.SeatedEvent;
import co.wadcorp.waiting.data.query.waiting.WaitingCountQueryRepository;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.settings.HomeSettingsService;
import co.wadcorp.waiting.data.service.waiting.WaitingManagementService;
import co.wadcorp.waiting.data.service.waiting.WaitingSeatCanceledHistoryService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Component
public class WaitingSeatedEventHandler {

  private final WaitingHistoryService waitingHistoryService;
  private final WaitingService waitingService;
  private final WaitingManagementService waitingManagementService;
  private final WaitingSeatCanceledHistoryService waitingSeatCanceledHistoryService;

  private final OrderService orderService;
  private final HomeSettingsService homeSettingsService;
  private final WaitingCountQueryRepository waitingCountQueryRepository;

  private final ApplicationEventPublisher eventPublisher;

  @Async
  @TransactionalEventListener(SeatedEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void seatedCancel(SeatedEvent event) {

    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(event.waitingHistorySeq());

    if (waitingHistory.getCustomerSeq() == null) { // 고객 정보가 없다면 취소할 수 없다.
      return;
    }

    List<WaitingEntity> waitings = waitingService.getWaitingByCustomerSeqToday(
        waitingHistory.getCustomerSeq(), event.operationDate()
    );

    if (waitings.isEmpty()) {
      return;
    }

    Map<Long, WaitingShopCurrentStatusSnapShot> currentStatusSnapMap = getWaitingSnapShotMap(
        waitings);
    List<WaitingHistoryEntity> canceledHistories = cancelOtherWaitings(waitingHistory, waitings);
    saveSeatCanceledHistories(waitingHistory.getWaitingSeq(), waitings, currentStatusSnapMap);

    // 주문이 있다면 같이 취소 처리
    waitings.forEach(waiting -> {
      OrderEntity orderEntity = orderService.getByWaitingId(waiting.getWaitingId());
      if (orderEntity == OrderEntity.EMPTY_ORDER) {
        return;
      }
      orderEntity.cancel();
    });

    // 착석 외 웨이팅 취소 알림톡 전송
    eventPublisher.publishEvent(
        new SeatCanceledEvent(event.shopId(), waitingHistory.getSeq(),
            canceledHistories.stream().map(WaitingHistoryEntity::getSeq).toList(),
            event.operationDate(),
            event.deviceId()
        )
    );
  }

  private Map<Long, WaitingShopCurrentStatusSnapShot> getWaitingSnapShotMap(
      List<WaitingEntity> waitings) {

    return waitings.stream()
        .map(waiting -> {
          int waitingTeamCount = waitingCountQueryRepository.countAllWaitingTeamLessThanOrEqualOrder(
              waiting.getShopId(), waiting.getOperationDate(), waiting.getWaitingOrder(),
              waiting.getSeatOptionName());
          HomeSettingsData homeSettings = homeSettingsService.getHomeSettings(waiting.getShopId())
              .getHomeSettingsData();

          return WaitingShopCurrentStatusSnapShot.of(waiting, homeSettings, waitingTeamCount);
        })
        .collect(Collectors.toMap(WaitingShopCurrentStatusSnapShot::seq, waiting -> waiting));
  }

  private void saveSeatCanceledHistories(Long waitingSeq, List<WaitingEntity> waitings,
      Map<Long, WaitingShopCurrentStatusSnapShot> currentStatusSnapMap) {

    waitings.forEach(cancelHistory -> {
      WaitingShopCurrentStatusSnapShot snapShot = currentStatusSnapMap.get(
          cancelHistory.getSeq());

      waitingSeatCanceledHistoryService.save(
          WaitingSeatCanceledHistoryEntity.builder()
              .seatWaitingSeq(waitingSeq)
              .canceledWaitingSeq(cancelHistory.getSeq())
              .canceledWaitingTeamCount(snapShot.teamCount)
              .canceledWaitingExpectedWaitingPeriod(snapShot.expectedWaitingPeriod)
              .build()
      );
    });
  }

  private List<WaitingHistoryEntity> cancelOtherWaitings(WaitingHistoryEntity waitingHistory,
      List<WaitingEntity> waitings) {
    return waitings.stream()
        .filter(item -> !item.getSeq().equals(waitingHistory.getSeq()))
        .map(item -> waitingManagementService.cancelBySitting(item.getWaitingId()))
        .toList();
  }

  record WaitingShopCurrentStatusSnapShot(
      Long seq, Integer teamCount, Integer expectedWaitingPeriod
  ) {

    public static WaitingShopCurrentStatusSnapShot of(WaitingEntity waiting,
        HomeSettingsData homeSettings, int waitingTeamCount) {

      SeatOptions seatOptions = homeSettings.findSeatOptionsBySeatOptionName(
          waiting.getSeatOptionName());
      Integer expectedWaitingPeriod = getExpectedWaitingPeriod(seatOptions, waitingTeamCount);

      return new WaitingShopCurrentStatusSnapShot(waiting.getSeq(), waitingTeamCount,
          expectedWaitingPeriod);
    }

    private static Integer getExpectedWaitingPeriod(SeatOptions seatOptions, int waitingTeamCount) {
      if (seatOptions.isNotUseExpectedWaitingPeriod()) {
        return null;
      }

      return seatOptions.calculateExpectedWaitingPeriod(waitingTeamCount);
    }
  }

}
