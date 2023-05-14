package co.wadcorp.waiting.shared.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMapperUtils {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static <T> T readByObjectMapper(String json, Class<T> classType) {
    if (!StringUtils.hasText(json)) {
      return null;
    }

    try {
      return OBJECT_MAPPER.readValue(json, classType);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  public static String convertToJson(Object object) {
    try {
      return OBJECT_MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

}
