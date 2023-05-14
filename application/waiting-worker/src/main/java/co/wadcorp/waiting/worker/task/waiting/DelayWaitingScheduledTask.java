package co.wadcorp.waiting.worker.task.waiting;

import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.event.DelayedEvent;
import co.wadcorp.waiting.data.service.waiting.WaitingManagementService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import co.wadcorp.waiting.worker.task.ScheduledTask;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class DelayWaitingScheduledTask implements ScheduledTask {

  private final WaitingService waitingService;
  private final WaitingManagementService waitingManagementService;
  private final ApplicationEventPublisher applicationEventPublisher;

  public DelayWaitingScheduledTask(WaitingService waitingService,
      WaitingManagementService waitingManagementService,
      ApplicationEventPublisher applicationEventPublisher) {
    this.waitingService = waitingService;
    this.waitingManagementService = waitingManagementService;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Transactional
  @Scheduled(fixedRate = 60000)
  public void delayWaitingTask() {
    ZonedDateTime zonedDateTime = ZonedDateTimeUtils.ofSeoul(LocalDate.now(), LocalTime.now());
    LocalDate operationDate = OperationDateUtils.getOperationDate(zonedDateTime.toLocalDateTime());

    List<WaitingEntity> delayedWaiting = waitingService.findDelayedWaiting(
        operationDate,
        zonedDateTime);

    delayedWaiting.forEach(item -> {
      WaitingHistoryEntity savedHistory = waitingManagementService.delayed(item.getShopId(),
          item.getWaitingId(), operationDate);
      applicationEventPublisher.publishEvent(
          new DelayedEvent(savedHistory.getShopId(), savedHistory.getSeq(), operationDate));
    });
  }
}
