package de.webalf.slotbot.service.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.exception.IgnoreErrorResponseErrorHandler;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.RestTemplatesUtil;
import de.webalf.slotbot.util.bot.DiscordUserUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.util.ListUtils;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class DiscordApiService {
	private final DiscordProperties discordProperties;

	/**
	 * Returns the nickname for the given user(id) on the server or the username
	 *
	 * @param userId user to get name for
	 * @return nickname on server or username if not set
	 */
	@Cacheable("discordUsernames")
	public String getName(String userId) {
		return getUser(userId).getUsername();
	}

	@Cacheable("discordNicknames")
	public String getName(String userId, long guildId) {
		GuildMember guildMember = getGuildMemberWithUser(userId, guildId);
		return guildMember.getNick();
	}

	private static final String UNKNOWN_USER_NAME = "Unbekannter Nutzer";
	/**
	 * @see <a href="https://discord.com/developers/docs/resources/user#get-user" target"_top">https://discord.com/developers/docs/resources/user#get-user</a>
	 */
	@Cacheable(cacheNames = "discordUser")
	public User getUser(String userId) {
		String url = "/users/" + userId;

		return buildWebClient().get().uri(url).retrieve().bodyToMono(User.class)
				.onErrorResume(error -> {
					log.error("Failed to get user {}", userId, error);
					final User errorUser = new User();
					errorUser.setUsername(UNKNOWN_USER_NAME);
					return Mono.just(errorUser);
				})
				.block();
	}

	public static boolean isUnknownUser(@NonNull User user) {
		return UNKNOWN_USER_NAME.equals(user.getUsername());
	}

	private boolean wait = false;
	private long waitUntil;

	/**
	 * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-member" target="_top">https://discord.com/developers/docs/resources/guild#get-guild-member</a>
	 */
	private synchronized GuildMember getGuildMember(String userId, long guildId) {
		String url = "/guilds/" + guildId + "/members/" + userId;

		if (wait) {
			try {
				TimeUnit.SECONDS.sleep(Math.max(0, waitUntil - (System.currentTimeMillis() / 1000)));
			} catch (InterruptedException e) {
				log.error("Sleep was interrupted", e);
			}
			wait = false;
		}

		final ResponseEntity<GuildMember> response = RestTemplatesUtil
				.get("https://discord.com/api/v8" + url, discordProperties.getToken(), new IgnoreErrorResponseErrorHandler(), GuildMember.class);
		final HttpHeaders headers = response.getHeaders();
		List<String> remainingHeaders = headers.get("x-ratelimit-remaining");
		List<String> resetAfterHeaders = headers.get("x-ratelimit-reset-after");
		if (!ListUtils.isEmpty(remainingHeaders) && !ListUtils.isEmpty(resetAfterHeaders) && "0".equals(remainingHeaders.get(0))) {
			wait = true;
			waitUntil = (System.currentTimeMillis() / 1000) + LongUtils.parseCeilLongFromDoubleString(resetAfterHeaders.get(0));
		}
		return response.getBody();
	}

	/**
	 * Returns the guild member. If not found it searches for the user itself and builds a {@link GuildMember}
	 *
	 * @param userId user to search for
	 * @param guildId guild of the user
	 * @return {@link GuildMember} with the given user
	 */
	@Cacheable("guildMember")
	public GuildMember getGuildMemberWithUser(String userId, long guildId) {
		GuildMember member = getGuildMember(userId, guildId);
		if (member.getUser() == null) {
			log.warn("Fetching user of id " + userId);
			User user = getUser(userId);
			member = GuildMember.builder().user(user).roles(Collections.emptySet()).build();
		}
		return member;
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
		private short discriminator;
		private String locale;

		public String getAvatarUrl() {
			return DiscordUserUtils.getAvatarUrl(Long.toString(id), avatar, Short.toString(discriminator));
		}
	}

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Role {
		private long id;
		private String name;
		private int position;
	}

	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GuildMember {
		private User user;
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
	}
}
