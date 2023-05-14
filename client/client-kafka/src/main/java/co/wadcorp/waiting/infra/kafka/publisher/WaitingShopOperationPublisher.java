package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingShopOperationMessage;
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
public class WaitingShopOperationPublisher {

  private final KafkaTemplate<String, WaitingShopOperationMessage> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting-shop-operation.name}")
  private String topicName;

  public WaitingShopOperationPublisher(
      KafkaTemplate<String, WaitingShopOperationMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(Long shopSeq, LocalDate operationDate) {
    WaitingShopOperationMessage eventMessage = WaitingShopOperationMessage.newBuilder()
        .setShopSeq(shopSeq)
        .setOperationDate(LocalDateUtils.convertToString(operationDate))
        .build();

    ProducerRecord<String, WaitingShopOperationMessage> producerRecord = new ProducerRecord<>(topicName, shopSeq.toString(), eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, shopSeq={}, X-TRACE-ID={}", producerRecord.topic(), shopSeq, uuid);
    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
