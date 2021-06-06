package de.webalf.slotbot.util.bot;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

import static de.webalf.slotbot.util.StringUtils.removeNonDigitCharacters;

/**
 * @author Alf
 * @since 11.01.2021
 */
@UtilityClass
public final class MentionUtils {
	private static final Pattern USER_MENTION = Pattern.compile("^<@!?\\d{17,18}>$");
	private static final Pattern CHANNEL_MENTION = Pattern.compile("^<#?\\d{17,18}>$");

	public static boolean isUserMention(String arg) {
		return USER_MENTION.matcher(arg).matches();
	}

	public static boolean isChannelMention(String arg) {
		return CHANNEL_MENTION.matcher(arg).matches();
	}

	public static String getId(String mention) {
		return removeNonDigitCharacters(mention);
	}
}
