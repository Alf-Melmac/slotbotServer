package de.webalf.slotbot.converter;

import de.webalf.slotbot.util.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Alf
 * @since 25.10.2020
 */
@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
	@Override
	public LocalDateTime convert(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		return LocalDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}
