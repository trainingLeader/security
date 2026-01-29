package com.bkseducate.securityapp.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

/**
 * Converter para UUID que lo almacena como CHAR(36) en la base de datos
 * Úsalo de forma explícita en campos que no sean @Id si lo necesitas
 */
@Converter(autoApply = false)
public class UuidConverter implements AttributeConverter<UUID, String> {
    
    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }
    
    @Override
    public UUID convertToEntityAttribute(String dbData) {
        return dbData == null ? null : UUID.fromString(dbData);
    }
}
