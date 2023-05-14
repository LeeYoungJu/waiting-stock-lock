package co.wadcorp.waiting.api.service.waiting.management;

import co.wadcorp.waiting.api.model.waiting.vo.CancelReason;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.CallWaitingServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.CancelWaitingServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.SittingWaitingServiceRequest;
import co.wadcorp.waiting.api.service.waiting.management.dto.request.UndoWaitingServiceRequest;
import co.wadcorp.waiting.data.domain.order.OrderEntity;
import co.wadcorp.waiting.data.domain.waiting.WaitingHistoryEntity;
import co.wadcorp.waiting.data.event.CalledEvent;
import co.wadcorp.waiting.data.event.CanceledByCustomerEvent;
import co.wadcorp.waiting.data.event.CanceledByOutOfStockEvent;
import co.wadcorp.waiting.data.event.CanceledByShopEvent;
import co.wadcorp.waiting.data.event.NoShowedEvent;
import co.wadcorp.waiting.data.event.SeatedEvent;
import co.wadcorp.waiting.data.event.UndoEvent;
import co.wadcorp.waiting.data.service.customer.ShopCustomerService;
import co.wadcorp.waiting.data.service.order.OrderService;
import co.wadcorp.waiting.data.service.waiting.WaitingManagementService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementWaitingApiService {

  private final WaitingManagementService service;
  private final ShopCustomerService shopCustomerService;
  private final ApplicationEventPublisher eventPublisher;

  private final OrderService orderService;

  @Transactional
  public void call(CallWaitingServiceRequest request) {
    WaitingHistoryEntity waitingHistory = service.call(
        request.getShopId(), request.getWaitingId(), request.getOperationDate()
    );

    eventPublisher.publishEvent(
        new CalledEvent(
            request.getShopId(), waitingHistory.getSeq(), request.getCurrentDateTime(),
            request.getDeviceId()
        )
    );
  }

  @Transactional
  public void sitting(SittingWaitingServiceRequest request) {
    WaitingHistoryEntity waitingHistory = service.sitting(
        request.getShopId(), request.getWaitingId(), request.getOperationDate()
    );
    shopCustomerService.sitting(
        request.getShopId(), waitingHistory.getCustomerSeq()
    );

    eventPublisher.publishEvent(
        new SeatedEvent(
            request.getShopId(), waitingHistory.getSeq(), request.getOperationDate(),
            request.getDeviceId()
        )
    );
  }

  @Transactional
  public void cancel(CancelWaitingServiceRequest request) {
    CancelReason cancelReason = request.getCancelReason();
    String shopId = request.getShopId();
    String waitingId = request.getWaitingId();
    LocalDate operationDate = request.getOperationDate();
    String deviceId = request.getDeviceId();


    if (cancelReason == CancelReason.CUSTOMER_REASON) {
      WaitingHistoryEntity waitingHistory = service.cancelByCustomer(shopId, waitingId);
      shopCustomerService.cancel(shopId, waitingHistory.getCustomerSeq());
      eventPublisher.publishEvent(new CanceledByCustomerEvent(shopId, waitingHistory.getSeq(),
          operationDate, deviceId));
    }

    if (cancelReason == CancelReason.SHOP_REASON) {
      WaitingHistoryEntity waitingHistory = service.cancelByShop(shopId, waitingId);
      shopCustomerService.cancel(shopId, waitingHistory.getCustomerSeq());
      eventPublisher.publishEvent(new CanceledByShopEvent(shopId, waitingHistory.getSeq(),
          operationDate, deviceId));
    }

    if (cancelReason == CancelReason.OUT_OF_STOCK_REASON) {
      WaitingHistoryEntity waitingHistory = service.cancelByOutOfStock(shopId, waitingId);
      shopCustomerService.cancel(shopId, waitingHistory.getCustomerSeq());
      eventPublisher.publishEvent(
          new CanceledByOutOfStockEvent(shopId, waitingHistory.getSeq(), operationDate, deviceId));
    }

    if (cancelReason == CancelReason.NO_SHOW) {
      WaitingHistoryEntity waitingHistory = service.noShow(shopId, waitingId);
      shopCustomerService.noShow(shopId, waitingHistory.getCustomerSeq());
      eventPublisher.publishEvent(
          new NoShowedEvent(shopId, waitingHistory.getSeq(), operationDate, deviceId));
    }

    OrderEntity orderEntity = orderService.getByWaitingId(waitingId);
    orderService.cancel(orderEntity);
  }

  @Transactional
  public void undo(UndoWaitingServiceRequest request) {
    WaitingHistoryEntity waitingHistory = service.undo(request.getShopId(), request.getWaitingId(), request.getOperationDate());
    shopCustomerService.undo(request.getShopId(), waitingHistory.getCustomerSeq(),
        waitingHistory.getWaitingId());

    OrderEntity orderEntity = orderService.getByWaitingId(request.getWaitingId());
    orderService.undo(orderEntity);
    eventPublisher.publishEvent(
        new UndoEvent(request.getShopId(), waitingHistory.getSeq(), request.getOperationDate(), request.getDeviceId()));
  }
}
