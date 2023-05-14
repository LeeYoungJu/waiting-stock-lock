package co.wadcorp.waiting.infra.kafka.config;

import static io.apicurio.registry.serde.avro.AvroKafkaSerdeConfig.AVRO_DATUM_PROVIDER;
import static io.apicurio.registry.serde.avro.AvroKafkaSerdeConfig.USE_SPECIFIC_AVRO_READER;

import co.wadcorp.waiting.event.WaitingMenuStockMessage;
import co.wadcorp.waiting.event.WaitingMessageV2;
import co.wadcorp.waiting.event.WaitingShopOperationMessageV2;
import co.wadcorp.waiting.event.WaitingShopSettingMessage;
import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroDatumProvider;
import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

  private static final String REGISTRY_URI = "/apis/registry/v2";

  @Value(value = "${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Value(value = "${schema-registry.host}")
  private String registryHost;

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, WaitingShopSettingMessage> waitingShopSettingListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, WaitingShopSettingMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
    Map<String, Object> props = createProps();
    factory.setConsumerFactory(consumerFactory(props));
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, WaitingMessageV2> waitingMessageV2ListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, WaitingMessageV2> factory = new ConcurrentKafkaListenerContainerFactory<>();
    Map<String, Object> props = createProps();
    factory.setConsumerFactory(consumerFactory(props));
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, WaitingShopOperationMessageV2> waitingShopOperationV2ListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, WaitingShopOperationMessageV2> factory = new ConcurrentKafkaListenerContainerFactory<>();
    Map<String, Object> props = createProps();
    factory.setConsumerFactory(consumerFactory(props));
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, WaitingMenuStockMessage> waitingMenuStockListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, WaitingMenuStockMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
    Map<String, Object> props = createProps();
    factory.setConsumerFactory(consumerFactory(props));
    return factory;
  }

  private ConsumerFactory<String, Object> consumerFactory(Map<String, Object> configProps) {
    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  private Map<String, Object> createProps() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        bootstrapAddress);
    configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroKafkaDeserializer.class.getName());
    configProps.putIfAbsent(USE_SPECIFIC_AVRO_READER, true);


    configProps.put(SerdeConfig.REGISTRY_URL, registryHost + REGISTRY_URI);
    return configProps;
  }
}
