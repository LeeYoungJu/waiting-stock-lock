package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.event.ChangedMenuStockEvent;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingMenuStockPublisher;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChangedMenuStockKafkaPublishEventHandler {

  private final WaitingMenuStockPublisher waitingMenuStockPublisher;

  @Async
  @TransactionalEventListener(ChangedMenuStockEvent.class)
  public void event(ChangedMenuStockEvent event) {
    publish(event.shopId(), event.deviceId());
  }

  private void publish(String shopId, String deviceId) {
    log.info("메뉴 재고 변경 이벤트 shopId={}", shopId);
    waitingMenuStockPublisher.publish(shopId, deviceId);
  }
}
