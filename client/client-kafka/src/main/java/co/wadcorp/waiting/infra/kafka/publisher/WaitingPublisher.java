package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingMessage;
import co.wadcorp.waiting.event.WaitingShopOperationMessage;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 웨이팅 다건 발송 카프카 퍼블리셔
 */
@Slf4j
@Component
public class WaitingPublisher {

  private final KafkaTemplate<String, WaitingMessage> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting.name}")
  private String waitingTopicName;

  public WaitingPublisher(KafkaTemplate<String, WaitingMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(List<String> waitingIds) {
    WaitingMessage eventMessage = WaitingMessage.newBuilder()
        .setWaitingIds(waitingIds)
        .build();

    ProducerRecord<String, WaitingMessage> producerRecord = new ProducerRecord<>(waitingTopicName, waitingIds.get(0), eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, waitingIdKey={}, X-TRACE-ID={}", producerRecord.topic(), waitingIds.get(0), uuid);
    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
