package co.wadcorp.waiting.infra.kafka.publisher;

import co.wadcorp.waiting.event.WaitingShopSettingMessage;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WaitingShopSettingPublisher {

  private final KafkaTemplate<String, WaitingShopSettingMessage> kafkaTemplate;

  @Value(value = "${spring.kafka.topics.waiting-shop-setting.name}")
  private String topicName;

  public WaitingShopSettingPublisher(
      KafkaTemplate<String, WaitingShopSettingMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(String shopId, String settingType, String deviceId) {
    WaitingShopSettingMessage eventMessage = WaitingShopSettingMessage.newBuilder()
        .setShopId(shopId)
        .setSettingType(settingType)
        .setDeviceId(deviceId)
        .build();

    ProducerRecord<String, WaitingShopSettingMessage> producerRecord = new ProducerRecord<>(topicName, shopId, eventMessage);

    String uuid = UUID.randomUUID().toString();
    log.info("KafKa Send Topic topicName={}, shopId={}, X-TRACE-ID={}", producerRecord.topic(), shopId, uuid);
    producerRecord.headers().add("X-TRACE-ID", uuid.getBytes());
    kafkaTemplate.send(producerRecord);
  }

}
