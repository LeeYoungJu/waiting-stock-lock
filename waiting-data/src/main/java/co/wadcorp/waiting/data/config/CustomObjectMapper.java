package co.wadcorp.waiting.data.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.LocalTime;

public class CustomObjectMapper extends ObjectMapper {
  public CustomObjectMapper() {
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    this.setVisibility(this.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

    SimpleModule module = new SimpleModule();
    module.addSerializer(LocalTime.class, new TimeSerializer());
    module.addDeserializer(LocalTime.class, new TimeDeserializer());

    registerModule(module);
  }
}
