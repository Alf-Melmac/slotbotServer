package de.webalf.slotbot.service.external;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.service.PermissionService;
import de.webalf.slotbot.util.LongUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.util.ListUtils;
import org.thymeleaf.util.SetUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class DiscordApiService {
	private final DiscordProperties discordProperties;

	private static final Set<String> KNOWN_ROLE_NAMES = new HashSet<>();
	private List<Role> roles = new ArrayList<>();

	private static final String ROLE_ADMINISTRATOR = "Administrator";
	private static final String ROLE_MODERATOR = "Moderator";
	private static final String ROLE_CREATOR = "Creator";
	private static final String ROLE_ARMA = "ArmA";

	static {
		KNOWN_ROLE_NAMES.add(ROLE_ADMINISTRATOR);
		KNOWN_ROLE_NAMES.add(ROLE_MODERATOR);
		KNOWN_ROLE_NAMES.add(ROLE_CREATOR);
		KNOWN_ROLE_NAMES.add(ROLE_ARMA);
	}

	/**
	 * Returns the role name that should be used for the given discord User
	 *
	 * @param userId user to get role for
	 * @return authorization role name
	 */
	@Cacheable("highestDiscordRole")
	public String getRole(String userId) {
		GuildMember member = getGuildMemberWithUser(userId);
		log.info("Login of: [" + userId + "] " + member.getUser().getUsername());

		Role highestRole = getHighestRole(member.getRoles());
		return "ROLE_" + getRoleName(highestRole);
	}

	/**
	 * Returns the nickname for the given user(id) on the server or the username
	 *
	 * @param userId user to get name for
	 * @return nickname on server or username if not set
	 */
	@Cacheable("discordNicknames")
	public String getName(String userId) {
		GuildMember guildMember = getGuildMemberWithUser(userId);
		return guildMember.getNick();
	}

	/**
	 * @see <a href="https://discord.com/developers/docs/resources/user#get-user" target"_top">https://discord.com/developers/docs/resources/user#get-user</a>
	 */
	private User getUser(String userId) {
		String url = "/users/" + userId;

		return buildWebClient().get().uri(url).retrieve().bodyToMono(User.class).block();
	}

	private boolean wait = false;
	private long waitUntil;

	/**
	 * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-member" target="_top">https://discord.com/developers/docs/resources/guild#get-guild-member</a>
	 */
	private synchronized GuildMember getGuildMember(String userId) {
		String url = "/guilds/" + discordProperties.getGuild() + "/members/" + userId;

		if (wait) {
			try {
				TimeUnit.SECONDS.sleep(Math.max(0, waitUntil - (System.currentTimeMillis() / 1000)));
			} catch (InterruptedException e) {
				log.error("Sleep was interrupted", e);
			}
			wait = false;
		}

		return buildWebClient().get().uri(url).exchange()
				.doOnSuccess(clientResponse -> {
					HttpHeaders httpHeaders = clientResponse.headers().asHttpHeaders();
					List<String> remainingHeaders = httpHeaders.get("x-ratelimit-remaining");
					List<String> resetAfterHeaders = httpHeaders.get("x-ratelimit-reset-after");
					if (!ListUtils.isEmpty(remainingHeaders) && !ListUtils.isEmpty(resetAfterHeaders) && "0".equals(remainingHeaders.get(0))) {
						wait = true;
						waitUntil = (System.currentTimeMillis() / 1000) + LongUtils.parseCeilLongFromDoubleString(resetAfterHeaders.get(0));
					}
				})
				.flatMap(clientResponse -> clientResponse.bodyToMono(GuildMember.class))
				.onErrorResume(error -> Mono.just(new GuildMember()))
				.block();
	}

	/**
	 * Returns the guild member. If not found it searches for the user itself and builds a {@link GuildMember}
	 *
	 * @param userId user to seach for
	 * @return {@link GuildMember} with the given user
	 */
	private GuildMember getGuildMemberWithUser(String userId) {
		GuildMember member = getGuildMember(userId);
		if (member.getUser() == null) {
			log.warn("Fetching user of id " + userId);
			User user = getUser(userId);
			member = GuildMember.builder().user(user).roles(Collections.emptySet()).build();
		}
		return member;
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

		Role userRole = Role.builder().name("USER").build();
		if (SetUtils.isEmpty(roleIds)) {
			return userRole;
		}
		return roles.stream().filter(role -> roleIds.contains(role.getId())).findFirst().orElse(userRole);
	}

	/**
	 * Return a string that fits to the given role. This string may be used for authorization
	 *
	 * @param role to map the name for
	 * @return role name corresponding to the given role
	 */
	private static String getRoleName(@NonNull Role role) {
		switch (role.getName()) {
			case ROLE_ADMINISTRATOR:
				return PermissionService.ADMIN;
			case ROLE_MODERATOR:
				return PermissionService.MOD;
			case ROLE_CREATOR:
				return PermissionService.CREATOR;
			case ROLE_ARMA:
				return PermissionService.ARMA;
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
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	private static class GuildMember {
		private User user;
		private String nick;
		private Set<Long> roles;

		String getNick() {
			if (nick != null) {
				return nick;
			} else if (user != null) {
				return user.getUsername();
			} else {
				return null;
			}
		}
	}
}
