package de.webalf.slotbot.model.external.discord;

import de.webalf.slotbot.util.bot.DiscordUserUtils;
import lombok.*;
import net.dv8tion.jda.api.entities.User;

/**
 * Representation of a discord user
 *
 * @author Alf
 * @see User
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object">User Object</a>
 * @since 27.01.2023
 */
//This can't be @Value to allow RestTemplate to create an instance of this class
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscordUser {
	private long id;
	private String username;
	private String global_name;
	private String avatar;

	/**
	 * @see User#getEffectiveName()
	 */
	public String getEffectiveName() {
		return global_name != null ? global_name : username;
	}

	public String getAvatarUrl() {
		return DiscordUserUtils.getAvatarUrl(Long.toString(id), avatar);
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
