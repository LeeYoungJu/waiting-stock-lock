package co.wadcorp.waiting.api.service.waiting.web;

import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.shop.ShopEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.PutOffEvent;
import co.wadcorp.waiting.data.event.UndoEvent;
import co.wadcorp.waiting.data.exception.AppException;
import co.wadcorp.waiting.data.exception.ErrorCode;
import co.wadcorp.waiting.data.query.settings.DisablePutOffQueryRepository;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.shop.ShopService;
import co.wadcorp.waiting.data.service.waiting.WaitingChangeStatusService;
import co.wadcorp.waiting.data.service.waiting.WaitingNumberService;
import co.wadcorp.waiting.data.service.waiting.WaitingService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WaitingChangeStatusWebService {

  private static final String WAITING_WEB = "WAITING_WEB";

  private final WaitingChangeStatusService waitingChangeStatusByCustomerService;
  private final WaitingService waitingService;
  private final ShopCustomerService shopCustomerService;
  private final ShopService shopService;
  private final OrderService orderService;
  private final WaitingNumberService waitingNumberService;
  private final DisablePutOffQueryRepository disablePutOffQueryRepository;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public WebCancelResponse cancel(String waitingId, LocalDate operationDate) {
    WaitingHistoryEntity waitingHistory = waitingChangeStatusByCustomerService.cancelByCustomer(
        waitingId);
    shopCustomerService.cancel(waitingHistory.getShopId(), waitingHistory.getCustomerSeq());

    eventPublisher.publishEvent(
        new CanceledByCustomerEvent(waitingHistory.getShopId(), waitingHistory.getSeq(),
            operationDate, WAITING_WEB));

    ShopEntity shopEntity = shopService.findByShopId(waitingHistory.getShopId());

    OrderEntity orderEntity = orderService.getByWaitingId(waitingId);
    orderService.cancel(orderEntity);

    return new WebCancelResponse(
        waitingHistory.getWaitingId(), shopEntity.getShopName(), shopEntity.getShopAddress(),
        shopEntity.getShopTelNumber(), waitingHistory.getWaitingStatus(),
        waitingHistory.getWaitingDetailStatus()
    );
  }

  @Transactional
  public void putOff(String waitingId, LocalDate operationDate) {
    WaitingEntity waiting = waitingService.findByWaitingId(waitingId);
    String waitingShopId = waiting.getShopId();

    Integer currentWaitingOrder = waitingNumberService.getMaxWaitingOrder(waitingShopId,
        operationDate);
    if (waiting.isSameWaitingOrder(currentWaitingOrder)) { // 이미 마지막 순번이면 미루기 불가
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.COULD_NOT_PUT_OFF);
    }

    if (disablePutOffQueryRepository.isShopDisabledPutOff(waitingShopId)) { // 미루기 off 매장
      throw new AppException(HttpStatus.BAD_REQUEST, ErrorCode.DISABLE_PUT_OFF);
    }

    Long maxWaitingOrder = waitingNumberService.incrementGetWaitingOrder(waitingShopId,
        operationDate);

    WaitingHistoryEntity waitingHistory = waitingChangeStatusByCustomerService.putOff(waitingId,
        operationDate,
        maxWaitingOrder
    );

    eventPublisher.publishEvent(
        new PutOffEvent(
            waitingHistory.getShopId(),
            waitingHistory.getSeq(),
            operationDate,
            waiting.getWaitingOrder(),
            waitingHistory.getWaitingOrder(),
            WAITING_WEB
        )
    );
  }

  @Transactional
  public void undo(String waitingId, LocalDate operationDate) {

    WaitingHistoryEntity waitingHistory = waitingChangeStatusByCustomerService.undoByCustomer(
        waitingId, operationDate
    );
    shopCustomerService.undo(waitingHistory.getShopId(), waitingHistory.getCustomerSeq(),
        waitingHistory.getWaitingId());

    OrderEntity orderEntity = orderService.getByWaitingId(waitingId);
    orderService.undo(orderEntity);

    eventPublisher.publishEvent(
        new UndoEvent(waitingHistory.getShopId(), waitingHistory.getSeq(), operationDate, WAITING_WEB));
  }

}
