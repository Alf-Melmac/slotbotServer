package de.webalf.slotbot.model.external.discord;

import lombok.*;
import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a discord guild member
 *
 * @author Alf
 * @see net.dv8tion.jda.api.entities.Member
 * @since 27.01.2023
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscordGuildMember {
	private DiscordUser user;
	private String nick;
	private Set<Long> roles;

	public String getNick() {
		if (nick != null) {
			return nick;
		} else if (user != null) {
			return user.getUsername();
		} else {
			return null;
		}
	}

	public static DiscordGuildMember fromJda(@NonNull net.dv8tion.jda.api.entities.Member member) {
		return DiscordGuildMember.builder()
				.user(DiscordUser.fromJda(member.getUser()))
				.nick(member.getNickname())
				.roles(member.getRoles().stream().map(ISnowflake::getIdLong).collect(Collectors.toUnmodifiableSet()))
				.build();
	}
}
