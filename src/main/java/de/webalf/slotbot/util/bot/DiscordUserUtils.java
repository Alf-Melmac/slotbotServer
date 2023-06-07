package de.webalf.slotbot.util.bot;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

/**
 * @author Alf
 * @since 17.01.2021
 */
@UtilityClass
public final class DiscordUserUtils {
	/**
	 * Returns the private channel for the given user
	 *
	 * @return the matching private channel or null if it doesn't exist
	 */
	public static PrivateChannel getPrivateChannel(User user) {
		if (user != null && user.hasPrivateChannel()) {
			return user.openPrivateChannel().complete();
		}
		return null;
	}

	/**
	 * @see User#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(@NotBlank String id, String avatar) {
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return String.format(User.AVATAR_URL, id, avatar, avatar.startsWith("a_") ? "gif" : "png");
	}

	/**
	 * @see Member#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(@NotBlank String guild, @NotBlank String id, String avatar) {
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return String.format(Member.AVATAR_URL, guild, id, avatar, avatar.startsWith("a_") ? "gif" : "png");
	}

	private static String getDefaultAvatarUrl(@NonNull String id) {
		return UserSnowflake.fromId(id).getDefaultAvatarUrl();
	}
}
