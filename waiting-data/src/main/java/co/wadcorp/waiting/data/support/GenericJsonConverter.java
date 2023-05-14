package co.wadcorp.waiting.data.support;

import co.wadcorp.waiting.data.config.CustomObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;

@Slf4j
public class GenericJsonConverter<T> implements AttributeConverter<T, String> {

  private final CustomObjectMapper objectMapper = new CustomObjectMapper();

  @Override
  public String convertToDatabaseColumn(T object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("fail to serialize as object into Json : {}", object, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public T convertToEntityAttribute(String jsonStr) {
    try {
      Class<?> aClass =
          GenericTypeResolver.resolveTypeArgument(getClass(), GenericJsonConverter.class);
      return (T) objectMapper.readValue(jsonStr, aClass);
    } catch (IOException e) {
      log.error("fail to deserialize as Json into Object : {}", jsonStr, e);
      throw new RuntimeException(e);
    }
  }
}