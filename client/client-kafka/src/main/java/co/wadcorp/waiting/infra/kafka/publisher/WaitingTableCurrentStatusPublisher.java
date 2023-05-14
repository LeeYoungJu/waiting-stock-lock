package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingShopOperationMessage;
import co.wadcorp.waiting.event.WaitingTableCurrentStatusMessage;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WaitingTableCurrentStatusPublisher {

  private final KafkaTemplate<String, WaitingTableCurrentStatusMessage> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting-table-current-status.name}")
  private String topicName;

  public WaitingTableCurrentStatusPublisher(KafkaTemplate<String, WaitingTableCurrentStatusMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(Long shopSeq) {
    WaitingTableCurrentStatusMessage eventMessage = WaitingTableCurrentStatusMessage.newBuilder()
        .setShopSeq(shopSeq)
        .build();

    ProducerRecord<String, WaitingTableCurrentStatusMessage> producerRecord = new ProducerRecord<>(topicName, shopSeq.toString(), eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, shopSeq={}, X-TRACE-ID={}", producerRecord.topic(), shopSeq, uuid);

    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
