package de.webalf.slotbot.service.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.PermissionHelper;
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

import static de.webalf.slotbot.util.PermissionHelper.Role.getByDiscordRole;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class DiscordApiService {
	private final DiscordProperties discordProperties;

	public static final String ROLE_SERVER_ADMIN = "Server Admin";
	public static final String ROLE_ADMINISTRATOR = "Administrator";
	public static final String ROLE_MODERATOR = "Moderator";
	public static final String ROLE_CREATOR = "Creator";
	public static final String ROLE_ARMA = "ArmA";
	public static final String ROLE_EVERYONE = "@everyone";
	public static final Set<String> KNOWN_ROLE_NAMES = Set.of(ROLE_SERVER_ADMIN, ROLE_ADMINISTRATOR, ROLE_MODERATOR, ROLE_CREATOR, ROLE_ARMA, ROLE_EVERYONE);

	private List<Role> roles = new ArrayList<>();

	@Cacheable("discordRoles")
	public Set<String> getRoles(User user) {
		final GuildMember member = getGuildMemberWithUser(Long.toString(user.getId()));
		log.info("Login of: [" + user.getId() + "] " + member.getUser().getUsername());

		return getRoles(member.getRoles()).stream()
				.map(role -> "ROLE_" + getApplicationRoleName(role))
				.collect(Collectors.toUnmodifiableSet());
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
	 * Returns the matching {@link Role}s for the given role ids
	 *
	 * @param roleIds to get role objects for
	 * @return set of matching roles
	 */
	private Set<Role> getRoles(Set<Long> roleIds) {
		if (!SetUtils.isEmpty(roleIds)) {
			fillRoles();

			final Set<Role> roleSet = roles.stream()
					.filter(role -> roleIds.contains(role.getId()))
					.collect(Collectors.toUnmodifiableSet());

			if (!SetUtils.isEmpty(roleSet)) {
				return roleSet;
			}
		}

		return Collections.singleton(Role.builder().name("USER").build());
	}

	/**
	 * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-roles" target="_top">https://discord.com/developers/docs/resources/guild#get-guild-roles</a>
	 */
	private void fillRoles() {
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
	}

	/**
	 * Return a string that fits to the given role. This string may be used for authorization
	 *
	 * @param discordRole to map the name for
	 * @return role name corresponding to the given role
	 */
	private static String getApplicationRoleName(@NonNull Role discordRole) {
		final PermissionHelper.Role roleEnum = getByDiscordRole(discordRole.getName());
		return roleEnum != null ? roleEnum.getApplicationRole() : PermissionHelper.Role.EVERYONE.getApplicationRole();
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl("https://discord.com/api/v8")
				.defaultHeader("Authorization", discordProperties.getToken())
				.build();
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class User {
		private long id;
		private String username;
		private String avatar;
		private String locale;
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
