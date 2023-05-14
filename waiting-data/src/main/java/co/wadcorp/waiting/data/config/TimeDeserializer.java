package co.wadcorp.waiting.data.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

public class TimeDeserializer extends JsonDeserializer<LocalTime> {

  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

  @Override
  public LocalTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {
    String valueAsString = jsonParser.getValueAsString();
    if (StringUtils.isEmpty(valueAsString)) {
      return null;
    }
    return LocalTime.parse(valueAsString, TIME_FORMAT);
  }
}
