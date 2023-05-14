package co.wadcorp.waiting.websocket.consumer;

import co.wadcorp.waiting.event.WaitingShopOperationMessageV2;
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
public class WaitingShopOperationInfoConsumer {

  private static final String SUB_SHOPS_SHOP_ID = "/sub/shops/%s";
  private final SimpMessagingTemplate template;

  @KafkaListener(topics = "${spring.kafka.topics.waiting-shop-operation-v2.name}", groupId = "${waiting.topic.kafka.consumer.group-id}", containerFactory = "waitingShopOperationV2ListenerContainerFactory")
  public void listener(ConsumerRecord<String, WaitingShopOperationMessageV2> messageConsumerRecord) {
    WaitingShopOperationMessageV2 value = messageConsumerRecord.value();

    consume(value.getShopId(), value.getDeviceId());
  }

  private void consume(String shopId, String deviceId) {
    log.info("매장 운영 설정 변경 comsumer: shopId = {}, deviceId = {}", shopId, deviceId);

    template.convertAndSend(String.format(SUB_SHOPS_SHOP_ID, shopId),
        new ShopOperationMessage("OPERATION_INFO_CHANGED", deviceId));
  }

  @Getter
  @NoArgsConstructor
  static class ShopOperationMessage {

    private String event;
    private String deviceId;
    private Data data;

    public ShopOperationMessage(String event, String deviceId) {
      this.event = event;
      this.deviceId = deviceId;
      this.data = new Data(true);
    }

    @Getter
    @NoArgsConstructor
    static class Data {
      private Boolean isChanged;

      public Data(Boolean isChanged) {
        this.isChanged = isChanged;
      }
    }
  }
}
