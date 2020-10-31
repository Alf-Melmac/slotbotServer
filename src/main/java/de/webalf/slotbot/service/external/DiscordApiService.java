package de.webalf.slotbot.service.external;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.util.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

import static de.webalf.slotbot.service.PermissionService.*;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscordApiService {
	private final DiscordProperties discordProperties;

	private static final Set<String> KNOWN_ROLE_NAMES = new HashSet<>();
	private static List<Role> roles = new ArrayList<>();

	static {
		KNOWN_ROLE_NAMES.add("Administrator");
		KNOWN_ROLE_NAMES.add("Moderator");
		KNOWN_ROLE_NAMES.add("Creator");
		KNOWN_ROLE_NAMES.add("Gamer");
	}

	/**
	 * Returns the role name that should be used for the given discord User
	 *
	 * @param userId user to get role for
	 * @return authorization role name
	 */
	public String getRole(String userId) {
		Role highestRole = getHighestRole(getGuildMember(userId).getRoles());

		return "ROLE_" + getRoleName(highestRole);
	}

	/**
	 * Returns the nickname for the given user(id) on the server or the username
	 *
	 * @param userId user to get name for
	 * @return nickname on server or username if not set
	 */
	public String getName(String userId) {
		GuildMember guildMember = getGuildMember(userId);
		if (!StringUtils.isEmpty(guildMember.getNick())) {
			return guildMember.getNick();
		} else {
			return guildMember.getUser().getUsername();
		}
	}

	/**
	 * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-member" target="_top">https://discord.com/developers/docs/resources/guild#get-guild-member</a>
	 */
	private GuildMember getGuildMember(String userId) {
		String url = "/guilds/" + discordProperties.getGuild() + "/members/" + userId;

		return buildWebClient().get().uri(url)
				.retrieve()
				.bodyToMono(GuildMember.class)
				.block();
	}

	/**
	 * Returns the role with the highest position
	 *
	 * @param roleIds set of roles to check
	 * @return {@link Role} with highest position or role with name USER
	 * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-roles" target="_top">https://discord.com/developers/docs/resources/guild#get-guild-roles</a>
	 */
	private Role getHighestRole(Set<Long> roleIds) {
		if (ListUtils.isEmpty(roles)) {
			String url = "/guilds/" + discordProperties.getGuild() + "/roles";

			roles = buildWebClient().get().uri(url)
					.retrieve()
					.bodyToFlux(Role.class)
					.toStream()
					.sorted(Comparator.comparingInt(Role::getPosition).reversed())
					.filter(role -> KNOWN_ROLE_NAMES.contains(role.getName()))
					.collect(Collectors.toList());
		}

		return roles.stream().filter(role -> roleIds.contains(role.getId())).findFirst().orElseGet(() -> Role.builder().name("USER").build());
	}

	/**
	 * Return a string that fits to the given role. This string may be used for authorization
	 *
	 * @param role to map the name for
	 * @return role name corresponding to the given role
	 */
	private static String getRoleName(@NonNull Role role) {
		switch (role.getName()) {
			case "Administrator":
				return ADMIN;
			case "Moderator":
				return MOD;
			case "Creator":
				return CREATOR;
			case "Gamer":
				return GAMER;
			default:
				return "USER";
		}
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl("https://discord.com/api/v8")
				.defaultHeader("Authorization", discordProperties.getToken())
				.build();
	}

	@Getter
	@Setter
	private static class User {
		private long id;
		private String username;
		private String avatar;
	}

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	private static class Role {
		private long id;
		private String name;
		private int position;
	}

	@Getter
	@Setter
	private static class GuildMember {
		private User user;
		private String nick;
		private Set<Long> roles;

		String getNick() {
			return nick != null ? nick : user.getUsername();
		}
	}
}
