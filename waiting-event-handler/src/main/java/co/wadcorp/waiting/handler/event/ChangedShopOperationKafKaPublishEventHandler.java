package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.event.ShopOperationUpdatedEvent;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingShopOperationPublisherV2;
import co.wadcorp.waiting.shared.util.OperationDateUtils;
import java.time.LocalDate;
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
public class ChangedShopOperationKafKaPublishEventHandler {

  private final WaitingShopOperationPublisherV2 waitingShopOperationPublisherV2;

  @Async
  @TransactionalEventListener(ShopOperationUpdatedEvent.class)
  public void event(ShopOperationUpdatedEvent event) {

    publish(event.shopId(), event.deviceId());
  }

  private void publish(String shopId, String deviceId) {
    LocalDate operationDate = OperationDateUtils.getOperationDateFromNow();
    log.info("매장 운영정보 변경 이벤트 shopId={}, operationDate={}", shopId, operationDate);
    waitingShopOperationPublisherV2.publish(shopId, operationDate, deviceId);
  }

}
