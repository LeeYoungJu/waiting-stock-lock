package co.wadcorp.waiting.worker.task;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.domain.waiting.cancel.AutoCancelTargetEntity;
import co.wadcorp.waiting.data.event.NoShowedEvent;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.query.waiting.AutoCancelTargetQueryRepository;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.waiting.WaitingManagementService;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import co.wadcorp.waiting.shared.util.ZonedDateTimeUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AutoCancelScheduledTask implements ScheduledTask {

  private static final int CHUNK_SIZE = 100;
  private static final String WAITING_WORKER = "WAITING_WORKER";

  private final WaitingManagementService waitingManagementService;
  private final ShopCustomerService shopCustomerService;
  private final OrderService orderService;

  private final AutoCancelTargetQueryRepository autoCancelTargetQueryRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Scheduled(fixedRate = 3000)
  public void autoCancelTask() {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();

    List<AutoCancelTargetEntity> targetEntities = autoCancelTargetQueryRepository.findByExpectedTimeWithLimit(
        ZonedDateTimeUtils.nowOfSeoul(), CHUNK_SIZE);

    for (AutoCancelTargetEntity targetEntity : targetEntities) {
      String shopId = targetEntity.getShopId();

      noShow(targetEntity, operationDate, shopId);

      targetEntity.success();
    }
  }

  private void noShow(AutoCancelTargetEntity targetEntity, LocalDate operationDate, String shopId) {
    try {
      WaitingHistoryEntity waitingHistory = waitingManagementService.noShow(shopId,
          targetEntity.getWaitingId());
      shopCustomerService.noShow(shopId, waitingHistory.getCustomerSeq());

      OrderEntity orderEntity = orderService.getByWaitingId(waitingHistory.getWaitingId());
      orderService.cancel(orderEntity);

      eventPublisher.publishEvent(
          new NoShowedEvent(shopId, waitingHistory.getSeq(), operationDate, WAITING_WORKER));
    } catch (AppException e) {
      log.info("자동취소 불가하여 IGNORE 처리한 웨이팅: waitingId={}, message={}",
          targetEntity.getWaitingId(), e.getDisplayMessage());

      targetEntity.ignore();
    }
  }

}
