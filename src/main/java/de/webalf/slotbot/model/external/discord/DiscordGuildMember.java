package de.webalf.slotbot.model.external.discord;

import de.webalf.slotbot.util.bot.DiscordUserUtils;
import lombok.*;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a discord guild member
 *
 * @author Alf
 * @see net.dv8tion.jda.api.entities.Member
 * @since 27.01.2023
 */
//This can't be @Value to allow RestTemplate to create an instance of this class
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscordGuildMember {
	private DiscordUser user;
	private String nick;
	private String avatar;
	private Set<Long> roles;
	private long guild;

	/**
	 * @see Member#getEffectiveName()
	 */
	public String getEffectiveName() {
		if (nick != null) {
			return nick;
		} else if (user != null) {
			return user.getEffectiveName();
		} else {
			return null;
		}
	}

	public String getAvatarUrl() {
		if (avatar != null) {
			return DiscordUserUtils.getAvatarUrl(Long.toString(guild), Long.toString(user.getId()), avatar);
		}
		return user.getAvatarUrl();
	}

	public static DiscordGuildMember fromJda(@NonNull net.dv8tion.jda.api.entities.Member member) {
		return DiscordGuildMember.builder()
				.user(DiscordUser.fromJda(member.getUser()))
				.nick(member.getNickname())
				.avatar(member.getAvatarId())
				.roles(member.getUnsortedRoles().stream().map(ISnowflake::getIdLong).collect(Collectors.toUnmodifiableSet()))
				.guild(member.getGuild().getIdLong())
				.build();
	}
}
