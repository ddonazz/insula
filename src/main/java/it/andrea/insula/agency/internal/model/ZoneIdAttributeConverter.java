package it.andrea.insula.agency.internal.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.ZoneId;

@Converter
public class ZoneIdAttributeConverter implements AttributeConverter<ZoneId, String> {

    @Override
    public String convertToDatabaseColumn(ZoneId attribute) {
        return attribute != null ? attribute.getId() : null;
    }

    @Override
    public ZoneId convertToEntityAttribute(String dbData) {
        return dbData != null ? ZoneId.of(dbData) : null;
    }
}
