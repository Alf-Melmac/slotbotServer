package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.util.StaticContextAccessor;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static net.dv8tion.jda.api.interactions.DiscordLocale.ENGLISH_UK;

/**
 * @author Alf
 * @since 07.01.2023
 */
public class DiscordLocaleHelper {
	@Getter
	private final Locale locale;
	private final MessageSource messageSource;

	public static final DiscordLocaleHelper DEFAULT_LOCALE = new DiscordLocaleHelper(ENGLISH_UK);

	/**
	 * Initialize helper with a {@link DiscordLocale}
	 */
	public DiscordLocaleHelper(@NonNull DiscordLocale locale, @NonNull MessageSource messageSource) {
		this.locale = Locale.forLanguageTag(locale.getLocale());
		this.messageSource = messageSource;
	}

	/**
	 * Constructor in static context
	 */
	private DiscordLocaleHelper(@NonNull DiscordLocale locale) {
		this(locale, StaticContextAccessor.getBean(MessageSource.class));
	}

	/**
	 * {@link MessageSource#getMessage(String, Object[], Locale) Translate} the given key to the locale of this instance
	 *
	 * @param key  to identify desired text
	 * @param args placeholder values
	 * @return translated text
	 */
	public String t(@NonNull String key, Object... args) {
		return messageSource.getMessage(key, args, locale);
	}
}
