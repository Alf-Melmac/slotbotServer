package de.webalf.slotbot.model.external.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.webalf.slotbot.util.bot.DiscordUserUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Representation of a discord user
 *
 * @author Alf
 * @see net.dv8tion.jda.api.entities.User
 * @since 27.01.2023
 */
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordUser {
	private long id;
	private String username;
	private String avatar;
	private short discriminator;
	private String locale;

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
