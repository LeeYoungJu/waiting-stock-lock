package co.wadcorp.waiting.infra.kafka.config;

import co.wadcorp.waiting.event.WaitingMenuStockMessage;
import co.wadcorp.waiting.event.WaitingMessage;
import co.wadcorp.waiting.event.WaitingMessageV2;
import co.wadcorp.waiting.event.WaitingShopOperationMessage;
import co.wadcorp.waiting.event.WaitingShopOperationMessageV2;
import co.wadcorp.waiting.event.WaitingShopSettingMessage;
import co.wadcorp.waiting.event.WaitingTableMessage;
import co.wadcorp.waiting.event.WaitingTableCurrentStatusMessage;
import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {

  private static final String REGISTRY_URI = "/apis/registry/v2";

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Value(value = "${schema-registry.host}")
  private String registryHost;

  @Bean
  public KafkaTemplate<String, WaitingMessage> waitingShopSeqKafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  @Bean
  public KafkaTemplate<String, WaitingMessageV2> waitingV2KafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  @Bean
  public KafkaTemplate<String, WaitingTableMessage> waitingTableShopSeqKafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  @Bean
  public KafkaTemplate<String, WaitingShopOperationMessage> waitingShopOperationShopSeqKafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  @Bean
  public KafkaTemplate<String, WaitingShopOperationMessageV2> waitingShopOperationV2ShopIdKafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  @Bean
  public KafkaTemplate<String, WaitingTableCurrentStatusMessage> waitingTableStatusShopSeqKafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  @Bean
  public KafkaTemplate<String, WaitingShopSettingMessage> waitingShopSettingShopIdKafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  @Bean
  public KafkaTemplate<String, WaitingMenuStockMessage> waitingMenuStockShopIdKafkaTemplate() {
    Map<String, Object> configProps = createConfigMap();
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
  }

  private Map<String, Object> createConfigMap() {
    // 상세 내용 관련해서는 다음 링크 참고
    // https://www.apicur.io/registry/docs/apicurio-registry/2.3.x/getting-started/assembly-using-kafka-client-serdes.html#registry-serdes-config-producer_registry
    Map<String, Object> configProps = new HashMap<>();

    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        AvroKafkaSerializer.class.getName());
    configProps.put(SerdeConfig.REGISTRY_URL, registryHost + REGISTRY_URI);
    // The lookup strategy to find the global ID for the schema.
    configProps.put(SerdeConfig.FIND_LATEST_ARTIFACT, Boolean.TRUE);

    return configProps;
  }

}
