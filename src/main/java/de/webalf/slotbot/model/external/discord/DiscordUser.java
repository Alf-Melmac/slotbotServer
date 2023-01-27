package de.webalf.slotbot.model.external.discord;

import de.webalf.slotbot.util.bot.DiscordUserUtils;
import lombok.*;

/**
 * Representation of a discord user
 *
 * @author Alf
 * @see net.dv8tion.jda.api.entities.User
 * @since 27.01.2023
 */
//This can't be @Value to allow RestTemplate to create an instance of this class
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscordUser {
	private long id;
	private String username;
	private String avatar;
	private short discriminator;

	public String getAvatarUrl() {
		return DiscordUserUtils.getAvatarUrl(Long.toString(id), avatar, Short.toString(discriminator));
	}

	public static DiscordUser fromJda(@NonNull net.dv8tion.jda.api.entities.User jdaUser) {
		return DiscordUser.builder()
				.id(jdaUser.getIdLong())
				.username(jdaUser.getName())
				.avatar(jdaUser.getAvatarId())
				.discriminator(Short.parseShort(jdaUser.getDiscriminator()))
				.build();
	}
}
