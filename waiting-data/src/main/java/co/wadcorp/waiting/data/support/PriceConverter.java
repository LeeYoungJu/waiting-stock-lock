package co.wadcorp.waiting.data.support;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;

@Converter
public class PriceConverter implements AttributeConverter<Price, BigDecimal> {

  @Override
  public BigDecimal convertToDatabaseColumn(Price attribute) {
    return attribute.value();
  }

  @Override
  public Price convertToEntityAttribute(BigDecimal dbData) {
    return Price.of(dbData);
  }

}
