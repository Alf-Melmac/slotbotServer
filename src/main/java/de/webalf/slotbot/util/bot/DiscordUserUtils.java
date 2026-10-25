package de.webalf.slotbot.util.bot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;

/**
 * @author Alf
 * @since 17.01.2021
 */
@UtilityClass
public final class DiscordUserUtils {
	/**
	 * @see User#getEffectiveName()
	 */
	public static String getEffectiveName(String globalName, String username) {
		return globalName != null ? globalName : username;
	}

	/**
	 * @see User#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(long idLong, String avatar) {
		final String id = Long.toString(idLong);
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return DiscordAssets.userAvatar(ImageFormat.ANIMATED_WEBP, id, avatar).getUrl();
	}

	/**
	 * @see Member#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(long guildIdLong, long idLong, String avatar) {
		final String id = Long.toString(idLong);
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return DiscordAssets.memberAvatar(ImageFormat.ANIMATED_WEBP, Long.toString(guildIdLong), id, avatar).getUrl();
	}

	private static String getDefaultAvatarUrl(@NonNull String id) {
		return UserSnowflake.fromId(id).getDefaultAvatarUrl();
	}
}
