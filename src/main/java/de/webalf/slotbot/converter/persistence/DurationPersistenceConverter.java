package de.webalf.slotbot.converter.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

/**
 * Converts {@link Duration} to {@link String}.
 *
 * @author Alf
 * @since 20.11.2020
 */
@Converter
public class DurationPersistenceConverter implements AttributeConverter<Duration, String> {
	@Override
	public String convertToDatabaseColumn(Duration entityValue) {
		if (entityValue != null) {
			return entityValue.toString();
		}
		return null;
	}

	@Override
	public Duration convertToEntityAttribute(String databaseValue) {
		if (databaseValue != null) {
			return Duration.parse(databaseValue);
		}
		return null;
	}
}