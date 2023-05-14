package co.wadcorp.waiting.websocket.consumer;

import co.wadcorp.waiting.event.WaitingMenuStockMessage;
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
public class WaitingMenuStockConsumer {

  private static final String SUB_SHOPS_SHOP_ID = "/sub/shops/%s";
  private final SimpMessagingTemplate template;

  @KafkaListener(topics = "${spring.kafka.topics.waiting-menu-stock.name}", groupId = "${waiting.topic.kafka.consumer.group-id}", containerFactory = "waitingMenuStockListenerContainerFactory")
  public void listener(ConsumerRecord<String, WaitingMenuStockMessage> messageConsumerRecord) {
    WaitingMenuStockMessage value = messageConsumerRecord.value();

    consume(value.getShopId(), value.getDeviceId());
  }

  private void consume(String shopId, String deviceId) {
    log.info("메뉴 재고 변경 comsumer: shopId = {}, deviceId = {}", shopId, deviceId);

    template.convertAndSend(String.format(SUB_SHOPS_SHOP_ID, shopId),
        new MenuStockMessage("STOCK_CHANGED", deviceId));
  }

  @Getter
  @NoArgsConstructor
  static class MenuStockMessage {

    private String event;
    private String deviceId;
    private Data data;

    public MenuStockMessage(String event, String deviceId) {
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
