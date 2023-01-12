package de.webalf.slotbot.converter.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.regex.Pattern;

/**
 * Converts {@link Pattern} to {@link String}.
 *
 * @author Alf
 * @since 04.01.2022
 */
@Converter
public class PatternPersistenceConverter implements AttributeConverter<Pattern, String> {
	@Override
	public String convertToDatabaseColumn(Pattern entityValue) {
		if (entityValue != null) {
			return entityValue.pattern();
		}
		return null;
	}

	@Override
	public Pattern convertToEntityAttribute(String databaseValue) {
		if (databaseValue != null) {
			return Pattern.compile(databaseValue);
		}
		return null;
	}
}
