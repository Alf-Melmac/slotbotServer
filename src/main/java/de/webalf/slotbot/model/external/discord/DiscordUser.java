package de.webalf.slotbot.model.external.discord;

import de.webalf.slotbot.util.bot.DiscordUserUtils;
import lombok.Builder;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

/**
 * Representation of a discord user
 *
 * @author Alf
 * @see User
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object">User Object</a>
 * @since 27.01.2023
 */
@Builder
public record DiscordUser(long id, String username, String global_name, String avatar) {
	public static final String UNKNOWN_USER_NAME = "Unbekannter Nutzer";

	/**
	 * @see DiscordUserUtils#getEffectiveName(String, String)
	 */
	public String getEffectiveName() {
		return DiscordUserUtils.getEffectiveName(global_name, username);
	}

	/**
	 * @see DiscordUserUtils#getAvatarUrl(long, String)
	 */
	public String getAvatarUrl() {
		return DiscordUserUtils.getAvatarUrl(id, avatar);
	}

	public static DiscordUser fromJda(@NonNull User jdaUser) {
		return DiscordUser.builder()
				.id(jdaUser.getIdLong())
				.username(jdaUser.getName())
				.global_name(jdaUser.getGlobalName())
				.avatar(jdaUser.getAvatarId())
				.build();
	}
}
