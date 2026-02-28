package de.webalf.slotbot.util.bot;

import jakarta.validation.constraints.NotBlank;
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
	 * @see User#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(@NotBlank String id, String avatar) {
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return DiscordAssets.userAvatar(ImageFormat.ANIMATED_WEBP, id, avatar).getUrl();
	}

	/**
	 * @see Member#getEffectiveAvatarUrl()
	 */
	public static String getAvatarUrl(@NotBlank String guild, @NotBlank String id, String avatar) {
		if (avatar == null) {
			return getDefaultAvatarUrl(id);
		}
		return DiscordAssets.memberAvatar(ImageFormat.ANIMATED_WEBP, guild, id, avatar).getUrl();
	}

	private static String getDefaultAvatarUrl(@NonNull String id) {
		return UserSnowflake.fromId(id).getDefaultAvatarUrl();
	}
}
