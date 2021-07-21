package de.webalf.slotbot.util.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
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

	/**
	 * @param channelId to mention
	 * @return string that is a mention inside discord
	 */
	public static String getChannelAsMention(long channelId) {
		return getAsMention(Long.toString(channelId), MentionType.CHANNEL);
	}

	/**
	 * @param userId to mention
	 * @return string that is a mention inside discord
	 */
	public static String getUserAsMention(String userId) {
		return getAsMention(userId, MentionType.USER);
	}

	/**
	 * Mentions the given {@link MentionType} with the given id in discord
	 *
	 * @param id of the object to mention
	 * @param mentionType type that should be mentioned
	 * @return string that mentions inside discord
	 */
	private static String getAsMention(String id, MentionType mentionType) {
		return "<" + mentionType.getMentionPrefix() + id + ">";
	}

	@Getter
	@AllArgsConstructor
	private enum MentionType {
		CHANNEL("#"),
		USER("@");

		private final String mentionPrefix;
	}
}
