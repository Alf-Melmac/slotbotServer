package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * Utils class to work with {@link net.dv8tion.jda.api.EmbedBuilder}
 *
 * @author Alf
 * @since 10.01.2021
 */
@UtilityClass
public class EmbedUtils {
	public static EmbedBuilder addField(String name, String value, @NonNull EmbedBuilder embedBuilder) {
		return addField(name, value, false, embedBuilder);
	}

	public static EmbedBuilder addField(String name, String value, boolean inline, @NonNull EmbedBuilder embedBuilder) {
		if (StringUtils.isNotEmpty(value)) {
			return embedBuilder.addField(name, value, inline);
		}
		return embedBuilder;
	}
}
