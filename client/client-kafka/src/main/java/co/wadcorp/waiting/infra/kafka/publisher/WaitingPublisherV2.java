package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingMessage;
import co.wadcorp.waiting.event.WaitingMessageV2;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 웨이팅 단건 발송 카프카 퍼블리셔
 */
@Slf4j
@Component
public class WaitingPublisherV2 {

  private final KafkaTemplate<String, WaitingMessageV2> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting-v2.name}")
  private String waitingTopicName;

  public WaitingPublisherV2(KafkaTemplate<String, WaitingMessageV2> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(String eventType, String shopId, String waitingId, String deviceId) {
    WaitingMessageV2 eventMessage = WaitingMessageV2.newBuilder()
        .setEventType(eventType)
        .setShopId(shopId)
        .setWaitingId(waitingId)
        .setDeviceId(deviceId)
        .build();

    ProducerRecord<String, WaitingMessageV2> producerRecord = new ProducerRecord<>(waitingTopicName, shopId, eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, waitingIdKey={}, X-TRACE-ID={}", producerRecord.topic(), shopId, uuid);
    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
