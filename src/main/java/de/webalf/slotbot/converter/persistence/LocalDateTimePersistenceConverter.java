package de.webalf.slotbot.converter.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Converts {@link java.time.LocalDateTime} to {@link Long}.
 *
 * @author Alf
 * @since 22.06.2020
 */
@Converter
public class LocalDateTimePersistenceConverter implements
		AttributeConverter<LocalDateTime, Long> {

	@Override
	public Long convertToDatabaseColumn(LocalDateTime entityValue) {
		if (entityValue != null) {
			return entityValue.toEpochSecond(ZoneOffset.UTC);
		}
		return null;
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Long databaseValue) {
		if (databaseValue != null) {
			return LocalDateTime.ofEpochSecond(databaseValue, 0, ZoneOffset.UTC);
		}
		return null;
	}
}
