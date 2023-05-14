package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingMenuStockMessage;
import co.wadcorp.waiting.event.WaitingShopOperationMessageV2;
import co.wadcorp.waiting.shared.util.LocalDateUtils;
import java.time.LocalDate;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WaitingMenuStockPublisher {

  private final KafkaTemplate<String, WaitingMenuStockMessage> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting-menu-stock.name}")
  private String topicName;

  public WaitingMenuStockPublisher(
      KafkaTemplate<String, WaitingMenuStockMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(String shopId, String deviceId) {
    WaitingMenuStockMessage eventMessage = WaitingMenuStockMessage.newBuilder()
        .setShopId(shopId)
        .setDeviceId(deviceId)
        .build();

    ProducerRecord<String, WaitingMenuStockMessage> producerRecord = new ProducerRecord<>(topicName, shopId, eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, shopId={}, X-TRACE-ID={}", producerRecord.topic(), shopId, uuid);
    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
