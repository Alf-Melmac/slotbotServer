package de.webalf.slotbot.util.bot;

import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 11.01.2021
 */
@UtilityClass
public final class MentionUtils {
	public static boolean isUserMention(String arg) {
		return arg.matches("^<@!?\\d{17,18}>$");
	}

	public static String getUserId(String mention) {
		return mention.replaceAll("\\D", "");
	}
}
