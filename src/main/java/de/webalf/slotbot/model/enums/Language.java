package de.webalf.slotbot.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

/**
 * @author Alf
 * @since 12.02.2023
 */
@Getter
@RequiredArgsConstructor
public enum Language {
	DE(Locale.GERMANY),
	EN(Locale.UK);

	/**
	 * Locale of this language
	 */
	private final Locale regionalLocale;
}
