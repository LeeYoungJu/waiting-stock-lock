package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingShopOperationMessage;
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
public class WaitingShopOperationPublisherV2 {

  private final KafkaTemplate<String, WaitingShopOperationMessageV2> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting-shop-operation-v2.name}")
  private String topicName;

  public WaitingShopOperationPublisherV2(
      KafkaTemplate<String, WaitingShopOperationMessageV2> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(String shopId, LocalDate operationDate, String deviceId) {
    WaitingShopOperationMessageV2 eventMessage = WaitingShopOperationMessageV2.newBuilder()
        .setShopId(shopId)
        .setOperationDate(LocalDateUtils.convertToString(operationDate))
        .setDeviceId(deviceId)
        .build();

    ProducerRecord<String, WaitingShopOperationMessageV2> producerRecord = new ProducerRecord<>(topicName, shopId, eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, shopId={}, X-TRACE-ID={}", producerRecord.topic(), shopId, uuid);
    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
