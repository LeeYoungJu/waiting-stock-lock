package co.wadcorp.waiting.data.support;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Java의 Boolean 을 DB의 CHAR(1) Y/N 으로 변환한다.
 */
@Converter
public class BooleanYnConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return BooleanUtils.isTrue(attribute) ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "Y".equalsIgnoreCase(dbData);
    }
}
