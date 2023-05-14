package co.wadcorp.waiting.websocket.consumer;

import co.wadcorp.waiting.event.WaitingMessageV2;
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
public class WaitingMessageConsumer {

  private static final String SUB_SHOPS_SHOP_ID = "/sub/shops/%s";
  private final SimpMessagingTemplate template;

  @KafkaListener(topics = "${spring.kafka.topics.waiting-v2.name}", groupId = "${waiting.topic.kafka.consumer.group-id}", containerFactory = "waitingMessageV2ListenerContainerFactory")
  public void listener(ConsumerRecord<String, WaitingMessageV2> messageConsumerRecord) {
    WaitingMessageV2 value = messageConsumerRecord.value();

    consume(value.getShopId(), value.getDeviceId(), value.getEventType(), value.getWaitingId());
  }

  private void consume(String shopId, String deviceId, String eventType, String waitingId) {
    log.info("웨이팅 변경 comsumer: shopId = {}, waitingId = {}, event = {}, deviceId = {}",
        shopId, waitingId, eventType, deviceId);

    template.convertAndSend(String.format(SUB_SHOPS_SHOP_ID, shopId),
        new WaitingMessage(eventType, deviceId, waitingId));
  }

  @Getter
  @NoArgsConstructor
  static class WaitingMessage {

    private String event;
    private String deviceId;
    private Data data;

    public WaitingMessage(String event, String deviceId, String waitingId) {
      this.event = event;
      this.deviceId = deviceId;
      this.data = new Data(waitingId);
    }

    @Getter
    @NoArgsConstructor
    static class Data {

      private String waitingId;

      public Data(String waitingId) {
        this.waitingId = waitingId;
      }
    }
  }
}
