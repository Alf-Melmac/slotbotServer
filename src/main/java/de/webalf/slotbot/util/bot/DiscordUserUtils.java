package de.webalf.slotbot.util.bot;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;

/**
 * @author Alf
 * @since 17.01.2021
 */
@UtilityClass
public final class DiscordUserUtils {
	/**
	 * @see User#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(@NotBlank String id, String avatar) {
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return User.AVATAR_URL.formatted(id, avatar, avatar.startsWith("a_") ? "gif" : "png");
	}

	/**
	 * @see Member#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(@NotBlank String guild, @NotBlank String id, String avatar) {
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return Member.AVATAR_URL.formatted(guild, id, avatar, avatar.startsWith("a_") ? "gif" : "png");
	}

	private static String getDefaultAvatarUrl(@NonNull String id) {
		return UserSnowflake.fromId(id).getDefaultAvatarUrl();
	}
}
