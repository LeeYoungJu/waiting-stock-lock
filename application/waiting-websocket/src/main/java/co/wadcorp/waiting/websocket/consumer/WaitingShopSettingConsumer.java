package co.wadcorp.waiting.websocket.consumer;

import co.wadcorp.waiting.event.WaitingShopSettingMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WaitingShopSettingConsumer {

  private static final String SUB_SHOPS_SHOP_ID = "/sub/shops/%s";
  private final SimpMessagingTemplate template;

  @KafkaListener(topics = "${spring.kafka.topics.waiting-shop-setting.name}", groupId = "${waiting.topic.kafka.consumer.group-id}", containerFactory = "waitingShopSettingListenerContainerFactory")
  public void listener(ConsumerRecord<String, WaitingShopSettingMessage> messageConsumerRecord) {
    WaitingShopSettingMessage value = messageConsumerRecord.value();

    consume(value.getShopId(), value.getDeviceId(), value.getSettingType());
  }

  private void consume(String shopId, String deviceId, String settingType) {
    log.info("매장 설정 변경 comsumer: shopId = {}, settingType = {}, deviceId = {}", shopId,
        settingType, deviceId);

    template.convertAndSend(String.format(SUB_SHOPS_SHOP_ID, shopId),
        new ShopSettingMessage("SETTING_CHANGED", settingType, deviceId));
  }

  @Getter
  @NoArgsConstructor
  static class ShopSettingMessage {

    private String event;
    private String deviceId;
    private Data data;

    public ShopSettingMessage(String event, String type, String deviceId) {
      this.event = event;
      this.deviceId = deviceId;
      this.data = new Data(type, true);
    }

    @Getter
    @NoArgsConstructor
    static class Data {

      private String type;
      private Boolean isChanged;

      public Data(String type, Boolean isChanged) {
        this.type = type;
        this.isChanged = isChanged;
      }
    }
  }
}
