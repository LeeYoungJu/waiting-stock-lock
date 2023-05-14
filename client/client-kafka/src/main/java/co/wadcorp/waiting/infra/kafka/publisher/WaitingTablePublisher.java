package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingTableCurrentStatusMessage;
import co.wadcorp.waiting.event.WaitingTableMessage;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WaitingTablePublisher {

  private final KafkaTemplate<String, WaitingTableMessage> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting-table.name}")
  private String topicName;

  public WaitingTablePublisher(KafkaTemplate<String, WaitingTableMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(Long shopSeq) {
    WaitingTableMessage eventMessage = WaitingTableMessage.newBuilder()
        .setShopSeq(shopSeq)
        .build();

    ProducerRecord<String, WaitingTableMessage> producerRecord = new ProducerRecord<>(topicName, shopSeq.toString(), eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, shopSeq={}, X-TRACE-ID={}", producerRecord.topic(), shopSeq, uuid);

    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
