package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.domain.settings.AlarmSettingsEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryService;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.event.CalledEvent;
import co.wadcorp.waiting.data.service.settings.AlarmSettingsService;
import co.wadcorp.waiting.data.service.waiting.AutoCancelTargetService;
import java.time.ZonedDateTime;
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
public class WaitingAutoCancelByCalledEventHandler {

  private static final int CALLED_COUNT_OF_CANCEL_TRIGGER = 1;

  private final WaitingHistoryService waitingHistoryService;
  private final AlarmSettingsService alarmSettingsService;
  private final AutoCancelTargetService autoCancelTargetService;

  @Async
  @TransactionalEventListener(CalledEvent.class)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void saveAutoCancelTarget(CalledEvent event) {
    Long waitingHistorySeq = event.waitingHistorySeq();
    WaitingHistoryEntity waitingHistory = waitingHistoryService.findById(waitingHistorySeq);
    String waitingId = waitingHistory.getWaitingId();

    long callCount = waitingHistoryService.countCalledWaitingById(waitingId);
    if (callCount == CALLED_COUNT_OF_CANCEL_TRIGGER) {
      AlarmSettingsEntity alarmSettings = alarmSettingsService.getAlarmSettings(event.shopId());

      log.info("웨이팅 호출 횟수가 {}회입니다. waitingId={}, usedAutoCancel={}",
          CALLED_COUNT_OF_CANCEL_TRIGGER, waitingId, alarmSettings.usedAutoCancel()
      );

      createAutoCancelTargetIfCan(event.currentDateTime(), event.shopId(), waitingId,
          alarmSettings);
    }
  }

  private void createAutoCancelTargetIfCan(ZonedDateTime currentDateTime, String shopId,
      String waitingId, AlarmSettingsEntity alarmSettings) {
    if (alarmSettings.autoCancelOff()) {
      return;
    }

    autoCancelTargetService.save(AutoCancelTargetEntity.init(
        shopId, waitingId, calculateExpectedCancelDateTime(currentDateTime, alarmSettings)
    ));
  }

  private static ZonedDateTime calculateExpectedCancelDateTime(ZonedDateTime currentDateTime,
      AlarmSettingsEntity alarmSettings) {
    return currentDateTime.plusMinutes(alarmSettings.getAutoCancelPeriod());
  }

}
