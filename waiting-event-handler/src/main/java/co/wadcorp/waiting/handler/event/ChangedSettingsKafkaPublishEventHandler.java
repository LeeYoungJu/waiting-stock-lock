package co.wadcorp.waiting.handler.event;

import co.wadcorp.waiting.data.event.settings.ChangedAlarmSettingsEvent;
import co.wadcorp.waiting.data.event.settings.ChangedHomeSettingsEvent;
import co.wadcorp.waiting.data.event.settings.ChangedOperationTimeSettingsEvent;
import co.wadcorp.waiting.data.event.settings.ChangedOptionSettingsEvent;
import co.wadcorp.waiting.data.event.settings.ChangedPrecationSettingsEvent;
import co.wadcorp.waiting.infra.kafka.publisher.WaitingShopSettingPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChangedSettingsKafkaPublishEventHandler {

  private final WaitingShopSettingPublisher waitingShopSettingPublisher;

  @Async
  @TransactionalEventListener(ChangedHomeSettingsEvent.class)
  public void event(ChangedHomeSettingsEvent event) {
    publish("HOME_SETTING", event.shopId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(ChangedOperationTimeSettingsEvent.class)
  public void event(ChangedOperationTimeSettingsEvent event) {

    publish("OPERATION_TIME_SETTING", event.shopId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(ChangedOptionSettingsEvent.class)
  public void event(ChangedOptionSettingsEvent event) {

    publish("OPTION_SETTING", event.shopId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(ChangedAlarmSettingsEvent.class)
  public void event(ChangedAlarmSettingsEvent event) {

    publish("ALARM_SETTING", event.shopId(), event.deviceId());
  }

  @Async
  @TransactionalEventListener(ChangedPrecationSettingsEvent.class)
  public void event(ChangedPrecationSettingsEvent event) {

    publish("PRECAUTION_SETTING", event.shopId(), event.deviceId());
  }

  private void publish(String type, String shopId, String deviceId) {
    log.info("매장 설정 변경 이벤트 shopId={}, type={}", shopId, type);
    waitingShopSettingPublisher.publish(shopId, type, deviceId);
  }
}
